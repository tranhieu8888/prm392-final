using Microsoft.VisualBasic;
using System.ComponentModel.DataAnnotations;

namespace TeamApp.Domain;

public class Project
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    [Required, MaxLength(180)]
    public string Name { get; set; } = default!;

    public string? Description { get; set; }

    public bool IsPublic { get; set; } = false;
    public ProjectStatus Status { get; set; } = ProjectStatus.TODO;
    public Guid CreatedBy { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? UpdatedAt { get; set; }

    // Navigation
    public ICollection<ProjectMember> Members { get; set; } = new List<ProjectMember>();
    public ICollection<TaskItem> Tasks { get; set; } = new List<TaskItem>();
    public ICollection<Conversation>? Conversations { get; set; } = new List<Conversation>();
}
