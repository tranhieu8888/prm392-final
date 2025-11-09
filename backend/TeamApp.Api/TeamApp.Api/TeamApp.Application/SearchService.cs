using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using System.Threading.Tasks;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class SearchService
    {
        private readonly AppDbContext _db;
        public SearchService(AppDbContext db) { _db = db; }

        public async Task<SearchResultDto> GlobalAsync(Guid me, string q)
        {
            q = q?.Trim() ?? "";

            var myProjectIds = await _db.ProjectMembers
                .Where(pm => pm.UserId == me)
                .Select(pm => pm.ProjectId)
                .ToListAsync();

            var projects = await _db.ProjectMembers
                .Where(pm => pm.UserId == me && pm.Project.Name.Contains(q))
                .Select(pm => pm.Project)
                .OrderByDescending(p => p.CreatedAt)
                .Select(p => new ProjectDto(p.Id, p.Name, p.Description, p.IsPublic, p.CreatedAt))
                .Take(20).ToListAsync();

            var tasks = await _db.Tasks
                .Where(t => myProjectIds.Contains(t.ProjectId) && t.Title.Contains(q))
                .OrderByDescending(t => t.UpdatedAt)
                .Select(t => new TaskDto(t.Id, t.ProjectId, t.Title, t.Description, t.Status.ToString(), t.Position, t.DueDate, t.UpdatedAt))
                .Take(20).ToListAsync();

            var users = await _db.Users
                .Where(u => u.FullName.Contains(q) || u.Email.Contains(q))
                .OrderBy(u => u.FullName)
                .Select(u => new UserDto(u.Id, u.FullName, u.Email, u.AvatarUrl))
                .Take(20).ToListAsync();

            return new SearchResultDto(projects, tasks, users);
        }
    }
}
