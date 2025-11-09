using Microsoft.EntityFrameworkCore;
using System;
using System.Threading.Tasks;
using TeamApp.Infrastructure;

namespace TeamApp.Application
{
    public class ProfileService
    {
        private readonly AppDbContext _db;
        public ProfileService(AppDbContext db) { _db = db; }

        public async Task<UserDto> MeAsync(Guid me)
        {
            var u = await _db.Users.AsNoTracking()
                .FirstOrDefaultAsync(x => x.Id == me) ?? throw new Exception("Not found");
            return new UserDto(u.Id, u.FullName, u.Email, u.AvatarUrl);
        }

        public async Task UpdateProfileAsync(Guid me, string fullName, string? avatarUrl)
        {
            var u = await _db.Users.FirstOrDefaultAsync(x => x.Id == me) ?? throw new Exception("Not found");
            if (!string.IsNullOrWhiteSpace(fullName)) u.FullName = fullName.Trim();
            u.AvatarUrl = avatarUrl;
            u.UpdatedAt = DateTime.UtcNow;
            await _db.SaveChangesAsync();
        }

        public async Task ChangePasswordAsync(Guid me, string currentPassword, string newPassword)
        {
            var u = await _db.Users.FirstOrDefaultAsync(x => x.Id == me) ?? throw new Exception("Not found");
            if (!BCrypt.Net.BCrypt.Verify(currentPassword, u.PasswordHash))
                throw new Exception("Mật khẩu hiện tại không đúng");
            u.PasswordHash = BCrypt.Net.BCrypt.HashPassword(newPassword);
            u.UpdatedAt = DateTime.UtcNow;
            await _db.SaveChangesAsync();
        }
    }
}
