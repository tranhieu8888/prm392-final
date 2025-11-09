using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TeamApp.Domain;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class DeviceService
    {
        private readonly AppDbContext _db;
        public DeviceService(AppDbContext db) { _db = db; }

        public async Task SaveAsync(Guid me, string token, string? platform)
        {
            var existed = await _db.DeviceTokens.FirstOrDefaultAsync(x => x.FcmToken == token);
            if (existed == null)
            {
                _db.DeviceTokens.Add(new DeviceToken { UserId = me, FcmToken = token, Platform = platform, LastSeenAt = DateTime.UtcNow });
            }
            else
            {
                existed.UserId = me; existed.Platform = platform; existed.LastSeenAt = DateTime.UtcNow;
            }
            await _db.SaveChangesAsync();
        }
    }
}
