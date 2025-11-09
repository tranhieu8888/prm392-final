// com/teamapp/data/mapper/Mapper.java
package com.teamapp.data.mapper;

import androidx.annotation.Nullable;

import com.teamapp.data.dto.AuthDtos;
import com.teamapp.data.dto.CommentDtos;
import com.teamapp.data.dto.ConversationDtos;
import com.teamapp.data.dto.DeviceDtos; // hiện chưa dùng Entity
import com.teamapp.data.dto.JoinRequestDtos;
import com.teamapp.data.dto.MemberDtos;
import com.teamapp.data.dto.MessageDtos;
import com.teamapp.data.dto.NotificationDtos;
import com.teamapp.data.dto.ProjectDtos;
import com.teamapp.data.dto.TaskDtos;

import com.teamapp.data.entity.CommentEntity;
import com.teamapp.data.entity.ConversationEntity;
import com.teamapp.data.entity.ConversationMemberEntity;
import com.teamapp.data.entity.JoinRequestEntity;
import com.teamapp.data.entity.MessageEntity;
import com.teamapp.data.entity.NotificationEntity;
import com.teamapp.data.entity.PendingActionEntity; // nếu cần trong tương lai
import com.teamapp.data.entity.ProjectEntity;
import com.teamapp.data.entity.ProjectMemberEntity;
import com.teamapp.data.entity.TaskAssigneeEntity;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.entity.UserEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Mapper trung tâm: DTO <-> Entity cho Room và helpers.
 * Chú ý:
 * - Một số field REST không có (vd: senderName, lastMessageAt) → để null, sẽ bù bằng realtime.
 * - Dùng safe() tránh null string.
 */
public final class Mapper {
    private Mapper() {}

    /* ===================== User ===================== */

    public static UserEntity toUserEntity(AuthDtos.UserDto dto) {
        if (dto == null) return null;
        UserEntity e = new UserEntity();
        e.id = dto.id;
        e.email = safe(dto.email);
        e.fullName = safe(dto.fullName);
        e.avatarUrl = dto.avatarUrl;
        return e;
    }

    public static AuthDtos.UserDto toUserDto(UserEntity e) {
        if (e == null) return null;
        AuthDtos.UserDto dto = new AuthDtos.UserDto();
        dto.id = e.id;
        dto.email = e.email;
        dto.fullName = e.fullName;
        dto.avatarUrl = e.avatarUrl;
        return dto;
    }

    /* ===================== Project ===================== */

    public static ProjectEntity toProjectEntity(ProjectDtos.ProjectDto dto) {
        if (dto == null) return null;
        ProjectEntity e = new ProjectEntity();
        e.id = dto.id;
        e.name = safe(dto.name);
        e.description = dto.description;
        e.isPublic = dto.isPublic;
        e.createdAt = dto.createdAt;
        return e;
    }

    public static ProjectDtos.ProjectDto toProjectDto(ProjectEntity e) {
        if (e == null) return null;
        ProjectDtos.ProjectDto dto = new ProjectDtos.ProjectDto();
        dto.id = e.id;
        dto.name = e.name;
        dto.description = e.description;
        dto.isPublic = e.isPublic;
        dto.createdAt = e.createdAt;
        return dto;
    }

    public static List<ProjectEntity> toProjectEntities(List<ProjectDtos.ProjectDto> list) {
        List<ProjectEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (ProjectDtos.ProjectDto dto : list) {
            ProjectEntity e = toProjectEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===== ProjectMember: map từ MemberDto (khi gọi /projects/{id}/members) ===== */

    public static ProjectMemberEntity toProjectMemberEntity(UUID projectId, MemberDtos.MemberDto dto) {
        if (dto == null) return null;
        ProjectMemberEntity e = new ProjectMemberEntity();
        e.projectId = projectId;
        e.userId = dto.userId;
        e.role = safe(dto.role);
        return e;
    }

    public static List<ProjectMemberEntity> toProjectMemberEntities(UUID projectId, List<MemberDtos.MemberDto> list) {
        List<ProjectMemberEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (MemberDtos.MemberDto m : list) {
            ProjectMemberEntity e = toProjectMemberEntity(projectId, m);
            if (e != null) out.add(e);
        }
        return out;
    }

    /** Lưu cache user song song (để hiện tên/ảnh offline) */
    public static UserEntity toUserEntity(MemberDtos.MemberDto dto) {
        if (dto == null) return null;
        UserEntity u = new UserEntity();
        u.id = dto.userId;
        u.fullName = safe(dto.fullName);
        u.email = safe(dto.email);
        u.avatarUrl = dto.avatarUrl;
        return u;
    }

    /* ===================== Task ===================== */

    public static TaskEntity toTaskEntity(TaskDtos.TaskDto dto) {
        if (dto == null) return null;
        TaskEntity e = new TaskEntity();
        e.id = dto.id;
        e.projectId = dto.projectId;
        e.title = safe(dto.title);
        e.description = dto.description;
        e.status = safe(dto.status);
        e.position = dto.position;
        e.dueDate = dto.dueDate;
        e.updatedAt = dto.updatedAt;
        return e;
    }

    public static TaskDtos.TaskDto toTaskDto(TaskEntity e) {
        if (e == null) return null;
        TaskDtos.TaskDto dto = new TaskDtos.TaskDto();
        dto.id = e.id;
        dto.projectId = e.projectId;
        dto.title = e.title;
        dto.description = e.description;
        dto.status = e.status;
        dto.position = e.position;
        dto.dueDate = e.dueDate;
        dto.updatedAt = e.updatedAt != null ? e.updatedAt : new Date();
        return dto;
    }

    public static List<TaskEntity> toTaskEntities(List<TaskDtos.TaskDto> list) {
        List<TaskEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (TaskDtos.TaskDto dto : list) {
            TaskEntity e = toTaskEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===== TaskAssignee: tạo từ danh sách assigneeIds của Task/CreateTask ===== */

    public static List<TaskAssigneeEntity> toTaskAssignees(UUID taskId, @Nullable List<UUID> assigneeIds) {
        List<TaskAssigneeEntity> out = new ArrayList<>();
        if (assigneeIds == null) return out;
        for (UUID uid : assigneeIds) {
            TaskAssigneeEntity a = new TaskAssigneeEntity();
            a.taskId = taskId;
            a.userId = uid;
            out.add(a);
        }
        return out;
    }

    /* ===================== Comment ===================== */

    // REST không trả authorName → để null; có thể bù từ cache User
    public static CommentEntity toCommentEntity(CommentDtos.CommentDto dto) {
        if (dto == null) return null;
        CommentEntity e = new CommentEntity();
        e.id = dto.id;
        e.taskId = dto.taskId;
        e.authorId = dto.authorId;
        e.authorName = null;
        e.content = safe(dto.content);
        e.createdAt = dto.createdAt;
        return e;
    }

    public static CommentDtos.CommentDto toCommentDto(CommentEntity e) {
        if (e == null) return null;
        CommentDtos.CommentDto dto = new CommentDtos.CommentDto();
        dto.id = e.id;
        dto.taskId = e.taskId;
        dto.authorId = e.authorId;
        dto.content = e.content;
        dto.createdAt = e.createdAt;
        return dto;
    }

    public static List<CommentEntity> toCommentEntities(List<CommentDtos.CommentDto> list) {
        List<CommentEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (CommentDtos.CommentDto dto : list) {
            CommentEntity e = toCommentEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===================== Conversation ===================== */

    // ConversationDto có createdAt (từ server bạn gửi) → ta vẫn để lastMessageAt null, sẽ update khi có message
    public static ConversationEntity toConversationEntity(ConversationDtos.ConversationDto dto) {
        if (dto == null) return null;
        ConversationEntity e = new ConversationEntity();
        e.id = dto.id;
        e.title = dto.title;
        e.type = dto.type;
        e.lastMessageAt = null;
        return e;
    }

    public static List<ConversationEntity> toConversationEntities(List<ConversationDtos.ConversationDto> list) {
        List<ConversationEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (ConversationDtos.ConversationDto dto : list) {
            ConversationEntity e = toConversationEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /** Nếu có message mới → cập nhật lastMessageAt */
    public static ConversationEntity updateConversationLastMessageAt(ConversationEntity c, @Nullable Date at) {
        if (c == null) return null;
        c.lastMessageAt = at != null ? at : new Date();
        return c;
    }

    /* ===== ConversationMember (nếu server trả) ===== */

    public static ConversationMemberEntity toConversationMemberEntity(UUID conversationId, ConversationDtos.ConversationMemberDto dto) {
        if (dto == null) return null;
        ConversationMemberEntity e = new ConversationMemberEntity();
        e.conversationId = conversationId;
        e.userId = dto.userId;
        e.lastReadMessageId = dto.lastReadMessageId;
        e.lastReadAt = dto.lastReadAt;
        return e;
    }

    public static List<ConversationMemberEntity> toConversationMemberEntities(UUID conversationId, List<ConversationDtos.ConversationMemberDto> list) {
        List<ConversationMemberEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (ConversationDtos.ConversationMemberDto m : list) {
            ConversationMemberEntity e = toConversationMemberEntity(conversationId, m);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===================== Message ===================== */

    // REST không có senderName
    public static MessageEntity toMessageEntity(MessageDtos.MessageDto dto) {
        if (dto == null) return null;
        MessageEntity e = new MessageEntity();
        e.id = dto.id;
        e.conversationId = dto.conversationId;
        e.senderId = dto.senderId;
        e.senderName = null;
        e.body = safe(dto.body);
        e.createdAt = dto.createdAt;
        return e;
    }

    public static MessageDtos.MessageDto toMessageDto(MessageEntity e) {
        if (e == null) return null;
        MessageDtos.MessageDto dto = new MessageDtos.MessageDto();
        dto.id = e.id;
        dto.conversationId = e.conversationId;
        dto.senderId = e.senderId;
        dto.body = e.body;
        dto.createdAt = e.createdAt;
        return dto;
    }

    public static List<MessageEntity> toMessageEntities(List<MessageDtos.MessageDto> list) {
        List<MessageEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (MessageDtos.MessageDto dto : list) {
            MessageEntity e = toMessageEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===== Helpers realtime (có senderName) ===== */

    public static MessageEntity toMessageEntityFromRealtime(
            UUID id,
            UUID conversationId,
            UUID senderId,
            @Nullable String senderName,
            String body,
            @Nullable Date createdAt
    ) {
        MessageEntity e = new MessageEntity();
        e.id = id;
        e.conversationId = conversationId;
        e.senderId = senderId;
        e.senderName = senderName;
        e.body = safe(body);
        e.createdAt = createdAt != null ? createdAt : new Date();
        return e;
    }

    /* ===================== Notification ===================== */

    public static NotificationEntity toNotificationEntity(NotificationDtos.NotificationDto dto) {
        if (dto == null) return null;
        NotificationEntity e = new NotificationEntity();
        e.id = dto.id;
        e.type = dto.type;
        e.dataJson = dto.dataJson;
        e.isRead = dto.isRead;
        e.createdAt = dto.createdAt;
        return e;
    }

    public static NotificationDtos.NotificationDto toNotificationDto(NotificationEntity e) {
        if (e == null) return null;
        NotificationDtos.NotificationDto dto = new NotificationDtos.NotificationDto();
        dto.id = e.id;
        dto.type = e.type;
        dto.dataJson = e.dataJson;
        dto.isRead = e.isRead;
        dto.createdAt = e.createdAt;
        return dto;
    }

    public static List<NotificationEntity> toNotificationEntities(List<NotificationDtos.NotificationDto> list) {
        List<NotificationEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (NotificationDtos.NotificationDto dto : list) {
            NotificationEntity e = toNotificationEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===================== JoinRequest ===================== */

    public static JoinRequestEntity toJoinRequestEntity(JoinRequestDtos.JoinRequestDto dto) {
        if (dto == null) return null;
        JoinRequestEntity e = new JoinRequestEntity();
        e.id = dto.id;
        e.projectId = dto.projectId;
        e.requesterId = dto.requesterId;
        e.status = safe(dto.status);
        e.createdAt = dto.createdAt;
        return e;
    }

    public static List<JoinRequestEntity> toJoinRequestEntities(List<JoinRequestDtos.JoinRequestDto> list) {
        List<JoinRequestEntity> out = new ArrayList<>();
        if (list == null) return out;
        for (JoinRequestDtos.JoinRequestDto dto : list) {
            JoinRequestEntity e = toJoinRequestEntity(dto);
            if (e != null) out.add(e);
        }
        return out;
    }

    /* ===================== Misc Utilities ===================== */

    private static String safe(String s) { return s == null ? "" : s; }
}
