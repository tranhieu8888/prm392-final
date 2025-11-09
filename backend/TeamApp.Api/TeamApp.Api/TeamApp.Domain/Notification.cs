using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(UserId), nameof(IsRead))]
public class Notification
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid UserId { get; set; }
    public User User { get; set; } = default!;

    public NotificationType Type { get; set; } = NotificationType.GENERIC;

    /// <summary>
    /// Payload JSON để deep-link (vd: {"projectId":"...","taskId":"..."}).
    /// </summary>
    [Required] public string DataJson { get; set; } = "{}";

    public bool IsRead { get; set; } = false;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
