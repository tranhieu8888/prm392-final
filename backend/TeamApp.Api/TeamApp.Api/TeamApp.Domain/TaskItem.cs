using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(ProjectId))]
[Index(nameof(Status))]
[Index(nameof(Position))]
public class TaskItem
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid ProjectId { get; set; }
    public Project Project { get; set; } = default!;

    [Required, MaxLength(220)]
    public string Title { get; set; } = default!;

    public string? Description { get; set; }

    public TaskStatus Status { get; set; } = TaskStatus.TODO;

    /// <summary>
    /// Giá trị số để sắp xếp trong Kanban (kỹ thuật "gán số giữa": 1000, 2000, chèn giữa = 1500).
    /// </summary>
    public double Position { get; set; } = 1000d;

    public DateTime? DueDate { get; set; }

    public Guid CreatedBy { get; set; }
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public ICollection<TaskAssignee> Assignees { get; set; } = new List<TaskAssignee>();
    public ICollection<Comment> Comments { get; set; } = new List<Comment>();
}
