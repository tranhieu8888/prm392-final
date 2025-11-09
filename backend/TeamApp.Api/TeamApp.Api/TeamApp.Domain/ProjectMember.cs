using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(ProjectId), nameof(UserId), IsUnique = true)]
public class ProjectMember
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid ProjectId { get; set; }
    public Project Project { get; set; } = default!;

    public Guid UserId { get; set; }
    public User User { get; set; } = default!;

    public ProjectRole Role { get; set; } = ProjectRole.Member;

    public DateTime JoinedAt { get; set; } = DateTime.UtcNow;
}
