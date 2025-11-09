using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(FcmToken), IsUnique = true)]
[Index(nameof(UserId))]
public class DeviceToken
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid UserId { get; set; }
    public User User { get; set; } = default!;

    [Required, MaxLength(1024)]
    public string FcmToken { get; set; } = default!;

    [MaxLength(32)]
    public string? Platform { get; set; } // "android", "ios", "web" (tùy chọn)

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? LastSeenAt { get; set; }
}
