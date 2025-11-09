using Microsoft.EntityFrameworkCore;
using TeamApp.Application.RealTime;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class MessageService
    {
        private readonly AppDbContext _db;
        private readonly ConversationService _convSvc;
        private readonly IRealtimeNotifier? _realtime; // abstraction, optional

        public MessageService(AppDbContext db, ConversationService convSvc, IRealtimeNotifier? realtime = null)
        {
            _db = db;
            _convSvc = convSvc;
            _realtime = realtime;
        }

        public async Task<List<MessageDto>> ListAsync(Guid me, Guid conversationId, DateTime? before = null, int pageSize = 50)
        {
            if (!await _convSvc.IsMemberAsync(me, conversationId)) throw new Exception("Forbidden");

            var q = _db.Messages.Where(m => m.ConversationId == conversationId);
            if (before != null) q = q.Where(m => m.CreatedAt < before);

            var items = await q.OrderByDescending(m => m.CreatedAt)
                .Take(pageSize)
                .Select(m => new MessageDto(m.Id, m.ConversationId, m.SenderId, m.Sender.FullName, m.Body, m.CreatedAt))
                .ToListAsync();

            items.Reverse(); // trả tăng dần thời gian
            return items;
        }

        public async Task<MessageDto> SendAsync(Guid me, Guid conversationId, string body)
        {
            if (string.IsNullOrWhiteSpace(body)) throw new Exception("Nội dung trống");
            if (!await _convSvc.IsMemberAsync(me, conversationId)) throw new Exception("Forbidden");

            var msg = new TeamApp.Domain.Message
            {
                ConversationId = conversationId,
                SenderId = me,
                Body = body
            };
            _db.Messages.Add(msg);
            await _db.SaveChangesAsync();

            var senderName = await _db.Users.Where(u => u.Id == me).Select(u => u.FullName).FirstAsync();
            var dto = new MessageDto(msg.Id, conversationId, me, senderName, msg.Body, msg.CreatedAt);

            // phát realtime nếu có implement (SignalR) được inject ở Web
            if (_realtime != null)
            {
                await _realtime.SendToConversationAsync(conversationId, "messageReceived", new
                {
                    conversationId,
                    message = dto
                });
            }

            return dto;
        }
    }
}
