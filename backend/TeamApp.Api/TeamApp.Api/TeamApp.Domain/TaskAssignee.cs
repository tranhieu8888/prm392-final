using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(TaskItemId), nameof(UserId), IsUnique = true)]
public class TaskAssignee
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid TaskItemId { get; set; }
    public TaskItem TaskItem { get; set; } = default!;

    public Guid UserId { get; set; }
    public User User { get; set; } = default!;
}
