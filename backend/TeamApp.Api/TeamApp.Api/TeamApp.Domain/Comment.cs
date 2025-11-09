using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(TaskItemId))]
public class Comment
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid TaskItemId { get; set; }
    public TaskItem TaskItem { get; set; } = default!;

    public Guid AuthorId { get; set; }
    public User Author { get; set; } = default!;

    [Required]
    public string Content { get; set; } = default!;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
