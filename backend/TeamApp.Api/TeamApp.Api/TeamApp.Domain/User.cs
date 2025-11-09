using System.ComponentModel.DataAnnotations;

using Microsoft.EntityFrameworkCore;

namespace TeamApp.Domain;

[Index(nameof(Email), IsUnique = true)]
public class User
{
    [Key] public Guid Id { get; set; } = Guid.NewGuid();

    [Required, MaxLength(120)]
    public string FullName { get; set; } = default!;

    [Required, MaxLength(160)]
    public string Email { get; set; } = default!;

    [Required]
    public string PasswordHash { get; set; } = default!;

    [MaxLength(512)]
    public string? AvatarUrl { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime? UpdatedAt { get; set; }
    public DateTime? LastLoginAt { get; set; }

    // Navigation
    public ICollection<ProjectMember> ProjectMembers { get; set; } = new List<ProjectMember>();
    public ICollection<TaskAssignee> TaskAssignees { get; set; } = new List<TaskAssignee>();
    public ICollection<Comment> Comments { get; set; } = new List<Comment>();
    public ICollection<ConversationMember> ConversationMembers { get; set; } = new List<ConversationMember>();
    public ICollection<Message> Messages { get; set; } = new List<Message>();
    public ICollection<Notification> Notifications { get; set; } = new List<Notification>();
    public ICollection<DeviceToken> DeviceTokens { get; set; } = new List<DeviceToken>();
}
