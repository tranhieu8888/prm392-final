using System.ComponentModel.DataAnnotations;
using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(ConversationId), nameof(CreatedAt))]
public class Message
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    public Guid ConversationId { get; set; }
    public Conversation Conversation { get; set; } = default!;

    public Guid SenderId { get; set; }
    public User Sender { get; set; } = default!;

    [Required]
    public string Body { get; set; } = default!;

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
}
