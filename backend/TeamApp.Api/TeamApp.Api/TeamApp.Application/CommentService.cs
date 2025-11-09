using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class CommentService
    {
        private readonly AppDbContext _db;
        public CommentService(AppDbContext db) { _db = db; }

        public async Task<List<CommentDto>> ListAsync(Guid me, Guid taskId, int page = 1, int pageSize = 50)
        {
            if (page < 1) page = 1;
            if (pageSize < 1) pageSize = 50;

            var task = await _db.Tasks.AsNoTracking().FirstOrDefaultAsync(t => t.Id == taskId)
                       ?? throw new Exception("Task not found");

            var isMember = await _db.ProjectMembers
                .AnyAsync(x => x.ProjectId == task.ProjectId && x.UserId == me);
            if (!isMember) throw new Exception("Forbidden");

            return await _db.Comments
                .Where(c => c.TaskItemId == taskId)
                .OrderBy(c => c.CreatedAt)
                .Skip((page - 1) * pageSize)
                .Take(pageSize)
                .Select(c => new CommentDto(
                    c.Id, c.TaskItemId, c.AuthorId, c.Author.FullName, c.Content, c.CreatedAt))
                .ToListAsync();
        }

        public async Task<CommentDto> AddAsync(Guid me, Guid taskId, string content)
        {
            if (string.IsNullOrWhiteSpace(content)) throw new Exception("Nội dung trống");

            var task = await _db.Tasks.FirstOrDefaultAsync(t => t.Id == taskId)
                       ?? throw new Exception("Task not found");

            var isMember = await _db.ProjectMembers
                .AnyAsync(x => x.ProjectId == task.ProjectId && x.UserId == me);
            if (!isMember) throw new Exception("Forbidden");

            var c = new Comment
            {
                TaskItemId = taskId,
                AuthorId = me,
                Content = content
            };
            _db.Comments.Add(c);
            await _db.SaveChangesAsync();

            var author = await _db.Users.AsNoTracking()
                            .Where(u => u.Id == me)
                            .Select(u => u.FullName)
                            .FirstAsync();

            return new CommentDto(c.Id, c.TaskItemId, c.AuthorId, author, c.Content, c.CreatedAt);
        }

        public async Task DeleteAsync(Guid me, Guid commentId)
        {
            var c = await _db.Comments.Include(x => x.Author).Include(x => x.TaskItem)
                    .FirstOrDefaultAsync(x => x.Id == commentId)
                    ?? throw new Exception("Not found");

            // Quyền xóa: tác giả hoặc Manager của project
            var isAuthor = c.AuthorId == me;
            var isManager = await _db.ProjectMembers.AnyAsync(pm =>
                pm.ProjectId == c.TaskItem.ProjectId && pm.UserId == me && pm.Role == ProjectRole.Manager);

            if (!isAuthor && !isManager) throw new Exception("Forbidden");

            _db.Comments.Remove(c);
            await _db.SaveChangesAsync();
        }
    }
}
