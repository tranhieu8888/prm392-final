using System.ComponentModel.DataAnnotations;

namespace TeamApp.Domain;

public class Conversation
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    /// <summary>
    /// Có thể gắn với 1 project (group chat dự án) hoặc null (DM toàn hệ thống).
    /// </summary>
    public Guid? ProjectId { get; set; }
    public Project? Project { get; set; }

    public ConversationType Type { get; set; }

    [MaxLength(180)]
    public string? Title { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

    // Navigation
    public ICollection<ConversationMember> Members { get; set; } = new List<ConversationMember>();
    public ICollection<Message> Messages { get; set; } = new List<Message>();
}
