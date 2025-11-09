using Microsoft.EntityFrameworkCore;

using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application;

public class ProjectService
{
    private readonly AppDbContext _db;
    public ProjectService(AppDbContext db) { _db = db; }

    public async Task<ProjectDto> CreateAsync(Guid me, CreateProjectRequest req)
    {
        var p = new Project { Name = req.Name, Description = req.Description, IsPublic = req.IsPublic, CreatedBy = me , Status= req.ProjectStatus};
        _db.Projects.Add(p);
        _db.ProjectMembers.Add(new ProjectMember { Project = p, UserId = me, Role = ProjectRole.Manager });
        await _db.SaveChangesAsync();
        return new ProjectDto(p.Id, p.Name, p.Description, p.IsPublic, p.CreatedAt, p.Status);
    }

    public Task<List<ProjectDto>> MyAsync(Guid me) =>
        _db.ProjectMembers.Where(pm => pm.UserId == me)
          .Select(pm => pm.Project).OrderByDescending(p => p.CreatedAt)
          .Select(p => new ProjectDto(p.Id, p.Name, p.Description, p.IsPublic, p.CreatedAt, p.Status))
          .ToListAsync();

    public Task<List<MemberDto>> MembersAsync(Guid projectId) =>
        _db.ProjectMembers.Where(x => x.ProjectId == projectId)
          .Select(x => new MemberDto(x.UserId, x.User.FullName, x.User.Email, x.User.AvatarUrl, x.Role.ToString()))
          .ToListAsync();

    public async Task InviteByEmailAsync(Guid me, Guid projectId, string email)
    {
        var isManager = await _db.ProjectMembers.AnyAsync(x => x.ProjectId == projectId && x.UserId == me && x.Role == ProjectRole.Manager);
        if (!isManager) throw new Exception("Forbidden");
        var user = await _db.Users.FirstOrDefaultAsync(x => x.Email == email) ?? throw new Exception("User không tồn tại");
        if (await _db.ProjectMembers.AnyAsync(x => x.ProjectId == projectId && x.UserId == user.Id)) return;
        _db.ProjectMembers.Add(new ProjectMember { ProjectId = projectId, UserId = user.Id, Role = ProjectRole.Member });
        await _db.SaveChangesAsync();
    }

    public Task<List<ProjectDto>> DiscoverAsync(Guid me, string? q) =>
        _db.Projects.Where(p => p.IsPublic && (q == null || p.Name.Contains(q)))
          .OrderByDescending(p => p.CreatedAt)
          .Select(p => new ProjectDto(p.Id, p.Name, p.Description, p.IsPublic, p.CreatedAt,p.Status))
          .ToListAsync();
}
