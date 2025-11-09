using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class ConversationService
    {
        private readonly AppDbContext _db;
        public ConversationService(AppDbContext db) { _db = db; }

        // Tạo/khởi tạo DM 1-1 (nếu đã tồn tại giữa 2 người thì trả về cái cũ)
        public async Task<Guid> StartDmAsync(Guid me, Guid otherUserId)
        {
            if (me == otherUserId) throw new Exception("Không thể DM với chính mình");

            // Tìm conversation DM có đúng 2 member là me & other
            var existing = await _db.Conversations
                .Where(c => c.Type == ConversationType.DM)
                .Where(c => c.Members.Count == 2 &&
                            c.Members.Any(m => m.UserId == me) &&
                            c.Members.Any(m => m.UserId == otherUserId))
                .Select(c => c.Id)
                .FirstOrDefaultAsync();

            if (existing != Guid.Empty) return existing;

            var conv = new Conversation { Type = ConversationType.DM, Title = null, ProjectId = null };
            _db.Conversations.Add(conv);
            _db.ConversationMembers.Add(new ConversationMember { Conversation = conv, UserId = me });
            _db.ConversationMembers.Add(new ConversationMember { Conversation = conv, UserId = otherUserId });
            await _db.SaveChangesAsync();
            return conv.Id;
        }

        // Tạo group (có thể gắn project)
        public async Task<Guid> CreateGroupAsync(Guid me, Guid? projectId, string title, List<Guid> memberIds)
        {
            if (string.IsNullOrWhiteSpace(title)) throw new Exception("Thiếu tiêu đề");
            memberIds = (memberIds ?? new()).Distinct().ToList();
            if (!memberIds.Contains(me)) memberIds.Add(me);

            // Nếu có ProjectId => ràng buộc tất cả thành viên phải là member project
            if (projectId != null)
            {
                var allowed = await _db.ProjectMembers
                    .Where(pm => pm.ProjectId == projectId)
                    .Select(pm => pm.UserId)
                    .ToListAsync();
                if (memberIds.Except(allowed).Any()) throw new Exception("Member không thuộc project");
            }

            var conv = new Conversation { Type = ConversationType.Group, Title = title, ProjectId = projectId };
            _db.Conversations.Add(conv);
            foreach (var uid in memberIds)
                _db.ConversationMembers.Add(new ConversationMember { Conversation = conv, UserId = uid });
            await _db.SaveChangesAsync();
            return conv.Id;
        }

        // Danh sách conversation của tôi + last message
        public async Task<List<ConversationDto>> MyConversationsAsync(Guid me, int page = 1, int pageSize = 50)
        {
            if (page < 1) page = 1;
            if (pageSize < 1) pageSize = 50;

            var convs = await _db.ConversationMembers
                .Where(cm => cm.UserId == me)
                .Select(cm => cm.Conversation)
                .OrderByDescending(c => c.Messages.Max(m => (DateTime?)m.CreatedAt) ?? c.CreatedAt)
                .Skip((page - 1) * pageSize).Take(pageSize)
                .Select(c => new
                {
                    c.Id,
                    c.Type,
                    c.ProjectId,
                    c.Title,
                    c.CreatedAt,
                    MemberCount = c.Members.Count,
                    LastMsg = c.Messages
                        .OrderByDescending(m => m.CreatedAt)
                        .Select(m => new { m.Id, m.SenderId, SenderName = m.Sender.FullName, m.Body, m.CreatedAt })
                        .FirstOrDefault()
                })
                .ToListAsync();

            return convs.Select(c => new ConversationDto(
                c.Id, c.Type.ToString(), c.ProjectId, c.Title, c.CreatedAt, c.MemberCount,
                c.LastMsg is null ? null : new MessagePreviewDto(c.LastMsg.Id, c.LastMsg.SenderId, c.LastMsg.SenderName, c.LastMsg.Body, c.LastMsg.CreatedAt)
            )).ToList();
        }

        // Kiểm tra quyền là member của conversation
        public Task<bool> IsMemberAsync(Guid me, Guid conversationId) =>
            _db.ConversationMembers.AnyAsync(x => x.ConversationId == conversationId && x.UserId == me);
    }
}
