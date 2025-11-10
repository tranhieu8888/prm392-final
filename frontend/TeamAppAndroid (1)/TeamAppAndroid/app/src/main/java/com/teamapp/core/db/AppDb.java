package com.teamapp.core.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.teamapp.core.db.dao.CommentDao;
import com.teamapp.core.db.dao.ConversationDao;
import com.teamapp.core.db.dao.ConversationMemberDao;
import com.teamapp.core.db.dao.JoinRequestDao;
import com.teamapp.core.db.dao.MessageDao;
import com.teamapp.core.db.dao.NotificationDao;
import com.teamapp.core.db.dao.PendingActionDao;
import com.teamapp.core.db.dao.ProjectDao;
import com.teamapp.core.db.dao.ProjectMemberDao;
import com.teamapp.core.db.dao.TaskAssigneeDao;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.core.db.dao.UserDao;
import com.teamapp.data.entity.CommentEntity;
import com.teamapp.data.entity.ConversationEntity;
import com.teamapp.data.entity.ConversationMemberEntity;
import com.teamapp.data.entity.JoinRequestEntity;
import com.teamapp.data.entity.MessageEntity;
import com.teamapp.data.entity.NotificationEntity;
import com.teamapp.data.entity.PendingActionEntity;
import com.teamapp.data.entity.ProjectEntity;
import com.teamapp.data.entity.ProjectMemberEntity;
import com.teamapp.data.entity.TaskAssigneeEntity;
import com.teamapp.data.entity.TaskEntity;
import com.teamapp.data.entity.UserEntity;

/**
 * Room database chính cho toàn app.
 * Dùng singleton: AppDb.create(context)
 */
@Database(
        entities = {
                TaskEntity.class,
                TaskAssigneeEntity.class,
                ProjectEntity.class,
                ProjectMemberEntity.class,
                JoinRequestEntity.class,
                ConversationMemberEntity.class,
                CommentEntity.class,
                ConversationEntity.class,
                MessageEntity.class,
                NotificationEntity.class,
                PendingActionEntity.class,
                UserEntity.class,

        },
        version = 2,
        exportSchema = false
)
@TypeConverters(DateConverters.class)
public abstract class AppDb extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract UserDao userDao();
    public abstract TaskAssigneeDao taskAssigneeDao();
    public abstract ConversationMemberDao conversationMemberDao();
    public abstract ProjectMemberDao projectMemberDao();
    public  abstract JoinRequestDao joinRequestDao();
    public abstract ProjectDao projectDao();
    public abstract CommentDao commentDao();
    public abstract ConversationDao conversationDao();
    public abstract MessageDao messageDao();
    public abstract NotificationDao notificationDao();
    public abstract PendingActionDao pendingActionDao();

    private static volatile AppDb INSTANCE;

    public static AppDb create(Context ctx) {
        if (INSTANCE == null) {
            synchronized (AppDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ctx.getApplicationContext(), AppDb.class, "teamapp.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
