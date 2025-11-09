using Microsoft.EntityFrameworkCore;

using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application;

public class JoinRequestService
{
    private readonly AppDbContext _db;
    public JoinRequestService(AppDbContext db) { _db = db; }

    public async Task<JoinRequestDto> RequestAsync(Guid me, Guid projectId)
    {
        var exists = await _db.JoinRequests.FirstOrDefaultAsync(x => x.ProjectId == projectId && x.RequesterId == me);
        if (exists != null && exists.Status == JoinStatus.PENDING) return ToDto(exists);

        var jr = exists ?? new JoinRequest { ProjectId = projectId, RequesterId = me };
        if (exists == null) _db.JoinRequests.Add(jr);
        jr.Status = JoinStatus.PENDING; jr.CreatedAt = DateTime.UtcNow;
        await _db.SaveChangesAsync();
        return ToDto(jr);
    }

    public async Task ApproveAsync(Guid managerId, Guid joinRequestId, bool approve)
    {
        var jr = await _db.JoinRequests.Include(x => x.Project).FirstOrDefaultAsync(x => x.Id == joinRequestId)
                 ?? throw new Exception("JoinRequest not found");
        var isManager = await _db.ProjectMembers.AnyAsync(x => x.ProjectId == jr.ProjectId && x.UserId == managerId && x.Role == ProjectRole.Manager);
        if (!isManager) throw new Exception("Forbidden");
        jr.Status = approve ? JoinStatus.APPROVED : JoinStatus.REJECTED;
        jr.ProcessedAt = DateTime.UtcNow; jr.ProcessedBy = managerId;
        await _db.SaveChangesAsync();

        if (approve && !await _db.ProjectMembers.AnyAsync(x => x.ProjectId == jr.ProjectId && x.UserId == jr.RequesterId))
        {
            _db.ProjectMembers.Add(new ProjectMember { ProjectId = jr.ProjectId, UserId = jr.RequesterId, Role = ProjectRole.Member });
            await _db.SaveChangesAsync();
        }
    }

    private static JoinRequestDto ToDto(JoinRequest x) =>
        new(x.Id, x.ProjectId, x.RequesterId, x.Status.ToString(), x.CreatedAt);
}
