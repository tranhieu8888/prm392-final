using System.ComponentModel.DataAnnotations.Schema;

namespace TeamApp.Domain;

/// <summary>
/// Composite Key (ConversationId, UserId) sẽ cấu hình bằng Fluent API trong DbContext.
/// </summary>
public class ConversationMember
{
    public Guid ConversationId { get; set; }
    public Conversation Conversation { get; set; } = default!;

    public Guid UserId { get; set; }
    public User User { get; set; } = default!;

    public Guid? LastReadMessageId { get; set; }
    public DateTime? LastReadAt { get; set; }
}
