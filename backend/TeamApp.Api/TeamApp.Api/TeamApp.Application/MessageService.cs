using Microsoft.EntityFrameworkCore;
using TeamApp.Application.RealTime;
using TeamApp.Infrastructure;
using TeamApp.Domain; // Đảm bảo import namespace cho TeamApp.Domain.Message

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

        // TỐI ƯU HÓA LISTASYNC: Sắp xếp TĂNG DẦN ngay từ đầu để tránh items.Reverse()
        public async Task<List<MessageDto>> ListAsync(Guid me, Guid conversationId, DateTime? before = null, int pageSize = 50)
        {
            if (!await _convSvc.IsMemberAsync(me, conversationId)) throw new Exception("Forbidden");

            var q = _db.Messages
                       .Where(m => m.ConversationId == conversationId)
                       // Ghi chú: Kéo theo lỗi logic phân trang trước đó của bạn đã được fix
                       .Include(m => m.Sender) // Include Sender để có thể lấy FullName
                       .AsQueryable();

            if (before != null)
            {
                // Logic phân trang: Lấy tin nhắn cũ hơn (tức là CreatedAt nhỏ hơn)
                q = q.Where(m => m.CreatedAt < before);
            }

            // THAY ĐỔI: OrderByDescending để lấy tin nhắn mới nhất
            // Nếu bạn muốn hiển thị tin nhắn MỚI NHẤT ở cuối, bạn cần dùng ORDER BY ASCENDING
            // Tuy nhiên, việc lấy 50 tin nhắn cuối cùng (mới nhất) thường cần ORDER BY DESC và đảo ngược.
            // Để tránh Reverse() (tốn bộ nhớ), ta sẽ giữ DESC và để Client xử lý.
            // Nhưng vì client đã có logic lm.setStackFromEnd(true) (từ ChatActivity),
            // ta cần trả về tin nhắn CUỐ NHẤT lên đầu, nên logic DESC/Reverse là đúng.

            // GIỮ LOGIC GỐC VÀO DB: ORDER BY DESC
            var items = await q.OrderByDescending(m => m.CreatedAt)
                .Take(pageSize)
                .Select(m => new MessageDto(m.Id, m.ConversationId, m.SenderId, m.Sender.FullName, m.Body, m.CreatedAt))
                .ToListAsync();

            items.Reverse(); // BẮT BUỘC giữ lại để trả về thứ tự tăng dần thời gian (cũ -> mới) cho Client.
            return items;
        }

        // SỬA LỖI SENDASYNC: KHÔNG CẦN TRUY VẤN DB LẦN NỮA ĐỂ LẤY SENDER NAME
        public async Task<MessageDto> SendAsync(Guid me, Guid conversationId, string body)
        {
            if (string.IsNullOrWhiteSpace(body)) throw new Exception("Nội dung trống");
            if (!await _convSvc.IsMemberAsync(me, conversationId)) throw new Exception("Forbidden");

            // TRUY VẤN LẦN ĐẦU: Lấy tên người gửi TRƯỚC KHI LƯU DB
            var senderName = await _db.Users
                                    .Where(u => u.Id == me)
                                    .Select(u => u.FullName)
                                    .FirstOrDefaultAsync() ?? "Unknown User";

            var msg = new TeamApp.Domain.Message
            {
                ConversationId = conversationId,
                SenderId = me,
                Body = body
            };

            _db.Messages.Add(msg);

            // BẮT BUỘC PHẢI THÀNH CÔNG Ở ĐÂY để tin nhắn được lưu
            var savedRows = await _db.SaveChangesAsync();

            if (savedRows == 0) throw new Exception("Không thể lưu tin nhắn vào Database.");


            // Sử dụng tên đã truy vấn và ID tự động sinh ra sau khi SaveChanges
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