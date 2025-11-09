using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class NotificationService
    {
        private readonly AppDbContext _db;
        private readonly FcmSender? _fcm;

        public NotificationService(AppDbContext db, FcmSender? fcm = null)
        {
            _db = db;
            _fcm = fcm;
        }

        public Task<List<NotificationDto>> ListAsync(Guid me) =>
            _db.Notifications.Where(n => n.UserId == me)
              .OrderByDescending(n => n.CreatedAt)
              .Select(n => new NotificationDto(n.Id, n.Type.ToString(), n.DataJson, n.IsRead, n.CreatedAt))
              .ToListAsync();

        public async Task MarkAsync(Guid me, Guid id, bool isRead)
        {
            var n = await _db.Notifications.FirstOrDefaultAsync(x => x.Id == id && x.UserId == me) ?? throw new Exception("Not found");
            n.IsRead = isRead;
            await _db.SaveChangesAsync();
        }

        public async Task CreateAsync(Guid userId, NotificationType type, string dataJson, string? title = null, string? body = null)
        {
            _db.Notifications.Add(new Notification { UserId = userId, Type = type, DataJson = dataJson });
            await _db.SaveChangesAsync();

            if (_fcm != null && (!string.IsNullOrWhiteSpace(title) || !string.IsNullOrWhiteSpace(body)))
            {
                var tokens = await _db.DeviceTokens
                    .Where(d => d.UserId == userId && !string.IsNullOrEmpty(d.FcmToken))
                    .Select(d => d.FcmToken)
                    .ToListAsync();

                var payload = new Dictionary<string, string> { { "type", type.ToString() }, { "data", dataJson } };

                foreach (var tk in tokens)
                {
                    try { await _fcm.SendAsync(tk, title ?? "", body ?? "", payload); }
                    catch { /* log nếu cần */ }
                }
            }
        }
    }
}
