using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class TaskService
    {
        private readonly AppDbContext _db;
        public TaskService(AppDbContext db) { _db = db; }

        public async Task<TaskDto> CreateAsync(Guid me, Guid projectId, CreateTaskRequest req)
        {
            var member = await _db.ProjectMembers.AnyAsync(x => x.ProjectId == projectId && x.UserId == me);
            if (!member) throw new Exception("Forbidden");

            var t = new TaskItem
            {
                ProjectId = projectId,
                Title = req.Title,
                Description = req.Description,
                DueDate = req.DueDate,
                Status = Domain.TaskStatus.TODO,
                Position = 1000,
                CreatedBy = me
            };
            _db.Tasks.Add(t);

            if (req.AssigneeIds != null)
            {
                foreach (var uid in req.AssigneeIds.Distinct())
                    _db.TaskAssignees.Add(new TaskAssignee { TaskItem = t, UserId = uid });
            }

            await _db.SaveChangesAsync();
            return ToDto(t);
        }

        public Task<List<TaskDto>> ByProjectAsync(Guid projectId) =>
            _db.Tasks.Where(t => t.ProjectId == projectId)
              .OrderBy(t => t.Status).ThenBy(t => t.Position).ThenByDescending(t => t.UpdatedAt)
              .Select(t => ToDto(t))
              .ToListAsync();

        public async Task UpdateStatusAsync(Guid me, Guid taskId, string status, double position)
        {
            var t = await _db.Tasks.FirstOrDefaultAsync(x => x.Id == taskId) ?? throw new Exception("Task not found");
            var isMember = await _db.ProjectMembers.AnyAsync(x => x.ProjectId == t.ProjectId && x.UserId == me);
            if (!isMember) throw new Exception("Forbidden");

            t.Status = Enum.Parse<Domain.TaskStatus>(status, ignoreCase: true);
            t.Position = position;
            t.UpdatedAt = DateTime.UtcNow;
            await _db.SaveChangesAsync();
        }

        // NEW: My Tasks
        public async Task<List<TaskDto>> MyTasksAsync(Guid me, string? status = null, int page = 1, int pageSize = 50)
        {
            if (page < 1) page = 1;
            if (pageSize < 1) pageSize = 50;

            var q = _db.TaskAssignees
                .Where(a => a.UserId == me)
                .Select(a => a.TaskItem)
                .AsQueryable();

            if (!string.IsNullOrWhiteSpace(status))
            {
                var st = Enum.Parse<Domain.TaskStatus>(status, ignoreCase: true);
                q = q.Where(t => t.Status == st);
            }

            return await q.OrderBy(t => t.DueDate)
                .ThenByDescending(t => t.UpdatedAt)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .Select(t => new TaskDto(t.Id, t.ProjectId, t.Title, t.Description, t.Status.ToString(), t.Position, t.DueDate, t.UpdatedAt))
                .ToListAsync();
        }

        private static TaskDto ToDto(TaskItem t) =>
            new(t.Id, t.ProjectId, t.Title, t.Description, t.Status.ToString(), t.Position, t.DueDate, t.UpdatedAt);
    }
}
