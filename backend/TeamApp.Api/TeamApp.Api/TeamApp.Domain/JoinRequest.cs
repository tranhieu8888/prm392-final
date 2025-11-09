using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(ProjectId), nameof(RequesterId), IsUnique = true)]
public class JoinRequest
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid ProjectId { get; set; }
    public Project Project { get; set; } = default!;

    public Guid RequesterId { get; set; }
    public User Requester { get; set; } = default!;

    public JoinStatus Status { get; set; } = JoinStatus.PENDING;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? ProcessedAt { get; set; }
    public Guid? ProcessedBy { get; set; }

    /// <remarks>
    /// Business rule: chỉ nên có 1 bản ghi PENDING cho (ProjectId, RequesterId).
    /// Unique index đảm bảo không tạo lặp; logic duyệt từ server cập nhật sang APPROVED/REJECTED.
    /// </remarks>
}
