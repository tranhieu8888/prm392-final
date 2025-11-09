using System;
using System.Collections.Generic;
using TeamApp.Domain;

namespace TeamApp.Application
{
    // ===== Auth =====
    public record RegisterRequest(string FullName, string Email, string Password);
    public record LoginRequest(string Email, string Password);
    public record UserDto(Guid Id, string FullName, string Email, string? AvatarUrl);
    public record AuthResponse(string Token, UserDto User);

    // ===== Projects =====
    public record CreateProjectRequest(string Name, string? Description, bool IsPublic,ProjectStatus ProjectStatus);
    public record ProjectDto(Guid Id, string Name, string? Description, bool IsPublic, DateTime CreatedAt,ProjectStatus ProjectStatus);

    // ===== Members =====
    public record MemberDto(Guid UserId, string FullName, string Email, string? AvatarUrl, string Role);

    // ===== Tasks =====
    public record CreateTaskRequest(string Title, string? Description, DateTime? DueDate, List<Guid>? AssigneeIds);
    public record TaskDto(Guid Id, Guid ProjectId, string Title, string? Description, string Status, double Position, DateTime? DueDate, DateTime UpdatedAt);
    public record UpdateTaskStatusRequest(string Status, double Position);

    // ===== MyTasks =====
    public record MyTasksQuery(string? Status = null, int Page = 1, int PageSize = 50);

    // ===== Comments =====
    public record CommentDto(Guid Id, Guid TaskId, Guid AuthorId, string AuthorName, string Content, DateTime CreatedAt);
    public record AddCommentRequest(string Content);

    // ===== Join Requests =====
    public record JoinRequestDto(Guid Id, Guid ProjectId, Guid RequesterId, string Status, DateTime CreatedAt);
    public record ApproveJoinRequestRequest(bool Approve);

    // ===== Notifications =====
    public record NotificationDto(Guid Id, string Type, string DataJson, bool IsRead, DateTime CreatedAt);
    public record MarkReadRequest(bool IsRead);

    // ===== Devices =====
    public record SaveDeviceTokenRequest(string FcmToken, string? Platform);

    // ===== Search + Calendar =====
    public record SearchResultDto(
        List<ProjectDto> Projects,
        List<TaskDto> Tasks,
        List<UserDto> Users
    );

    // ===== Chat / Conversations / Messages =====
    public record ConversationDto(Guid Id, string Type, Guid? ProjectId, string? Title, DateTime CreatedAt, int MemberCount, MessagePreviewDto? LastMessage);
    public record MessagePreviewDto(Guid Id, Guid SenderId, string SenderName, string Body, DateTime CreatedAt);
    public record MessageDto(Guid Id, Guid ConversationId, Guid SenderId, string SenderName, string Body, DateTime CreatedAt);

    public record StartDmRequest(Guid OtherUserId);
    public record CreateGroupRequest(Guid? ProjectId, string Title, List<Guid> MemberIds);
    public record SendMessageRequest(string Body);
}
