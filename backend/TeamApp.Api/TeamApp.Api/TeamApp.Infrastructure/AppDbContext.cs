using Microsoft.EntityFrameworkCore;
using Microsoft.VisualBasic;
using System.Collections.Generic;
using System.Reflection.Emit;
using System.Xml.Linq;
using TeamApp.Domain;

namespace TeamApp.Infrastructure;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> opt) : base(opt) { }

    public DbSet<User> Users => Set<User>();
    public DbSet<Project> Projects => Set<Project>();
    public DbSet<ProjectMember> ProjectMembers => Set<ProjectMember>();
    public DbSet<TaskItem> Tasks => Set<TaskItem>();
    public DbSet<TaskAssignee> TaskAssignees => Set<TaskAssignee>();
    public DbSet<Comment> Comments => Set<Comment>();
    public DbSet<Conversation> Conversations => Set<Conversation>();
    public DbSet<ConversationMember> ConversationMembers => Set<ConversationMember>();
    public DbSet<Message> Messages => Set<Message>();
    public DbSet<JoinRequest> JoinRequests => Set<JoinRequest>();
    public DbSet<Notification> Notifications => Set<Notification>();
    public DbSet<DeviceToken> DeviceTokens => Set<DeviceToken>();

    protected override void OnModelCreating(ModelBuilder b)
    {
        // Composite keys
        b.Entity<ConversationMember>().HasKey(x => new { x.ConversationId, x.UserId });

        // Enum as string for readability
        b.Entity<ProjectMember>().Property(x => x.Role).HasConversion<string>();
        b.Entity<TaskItem>().Property(x => x.Status).HasConversion<string>();
        b.Entity<Conversation>().Property(x => x.Type).HasConversion<string>();
        b.Entity<JoinRequest>().Property(x => x.Status).HasConversion<string>();
        b.Entity<Notification>().Property(x => x.Type).HasConversion<string>();

        // Relationships + delete behaviors
        b.Entity<ProjectMember>()
            .HasOne(pm => pm.Project).WithMany(p => p.Members)
            .HasForeignKey(pm => pm.ProjectId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<ProjectMember>()
            .HasOne(pm => pm.User).WithMany(u => u.ProjectMembers)
            .HasForeignKey(pm => pm.UserId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<TaskItem>()
            .HasOne(t => t.Project).WithMany(p => p.Tasks)
            .HasForeignKey(t => t.ProjectId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<TaskAssignee>()
            .HasOne(ta => ta.TaskItem).WithMany(t => t.Assignees)
            .HasForeignKey(ta => ta.TaskItemId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<TaskAssignee>()
            .HasOne(ta => ta.User).WithMany(u => u.TaskAssignees)
            .HasForeignKey(ta => ta.UserId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<Comment>()
            .HasOne(c => c.TaskItem).WithMany(t => t.Comments)
            .HasForeignKey(c => c.TaskItemId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<Comment>()
            .HasOne(c => c.Author).WithMany(u => u.Comments)
            .HasForeignKey(c => c.AuthorId).OnDelete(DeleteBehavior.Restrict);

        b.Entity<ConversationMember>()
            .HasOne(cm => cm.Conversation).WithMany(c => c.Members)
            .HasForeignKey(cm => cm.ConversationId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<ConversationMember>()
            .HasOne(cm => cm.User).WithMany(u => u.ConversationMembers)
            .HasForeignKey(cm => cm.UserId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<Message>()
            .HasOne(m => m.Conversation).WithMany(c => c.Messages)
            .HasForeignKey(m => m.ConversationId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<Message>()
            .HasOne(m => m.Sender).WithMany(u => u.Messages)
            .HasForeignKey(m => m.SenderId).OnDelete(DeleteBehavior.Restrict);

        b.Entity<JoinRequest>()
            .HasOne(j => j.Project).WithMany()
            .HasForeignKey(j => j.ProjectId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<JoinRequest>()
            .HasOne(j => j.Requester).WithMany()
            .HasForeignKey(j => j.RequesterId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<Notification>()
            .HasOne(n => n.User).WithMany(u => u.Notifications)
            .HasForeignKey(n => n.UserId).OnDelete(DeleteBehavior.Cascade);

        b.Entity<DeviceToken>()
            .HasOne(d => d.User).WithMany(u => u.DeviceTokens)
            .HasForeignKey(d => d.UserId).OnDelete(DeleteBehavior.Cascade);
    }
}
