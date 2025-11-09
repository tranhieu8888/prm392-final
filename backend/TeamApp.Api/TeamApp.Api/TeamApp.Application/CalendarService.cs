using Microsoft.EntityFrameworkCore;

using TeamApp.Infrastructure;

namespace TeamApp.Application;

public class CalendarService
{
    private readonly AppDbContext _db;
    public CalendarService(AppDbContext db) { _db = db; }

    public async Task<List<TaskDto>> TasksInMonthAsync(Guid me, int year, int month)
    {
        var start = new DateTime(year, month, 1);
        var end = start.AddMonths(1);

        // Lọc task của các project mà user là member
        var myProjectIds = await _db.ProjectMembers
            .Where(pm => pm.UserId == me).Select(pm => pm.ProjectId).ToListAsync();

        return await _db.Tasks
            .Where(t => myProjectIds.Contains(t.ProjectId) && t.DueDate >= start && t.DueDate < end)
            .OrderBy(t => t.DueDate)
            .Select(t => new TaskDto(t.Id, t.ProjectId, t.Title, t.Description, t.Status.ToString(), t.Position, t.DueDate, t.UpdatedAt))
            .ToListAsync();
    }
}
