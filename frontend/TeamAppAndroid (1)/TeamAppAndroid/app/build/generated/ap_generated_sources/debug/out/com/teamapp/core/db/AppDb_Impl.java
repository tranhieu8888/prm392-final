package com.teamapp.core.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.teamapp.core.db.dao.CommentDao;
import com.teamapp.core.db.dao.CommentDao_Impl;
import com.teamapp.core.db.dao.ConversationDao;
import com.teamapp.core.db.dao.ConversationDao_Impl;
import com.teamapp.core.db.dao.ConversationMemberDao;
import com.teamapp.core.db.dao.ConversationMemberDao_Impl;
import com.teamapp.core.db.dao.JoinRequestDao;
import com.teamapp.core.db.dao.JoinRequestDao_Impl;
import com.teamapp.core.db.dao.MessageDao;
import com.teamapp.core.db.dao.MessageDao_Impl;
import com.teamapp.core.db.dao.NotificationDao;
import com.teamapp.core.db.dao.NotificationDao_Impl;
import com.teamapp.core.db.dao.PendingActionDao;
import com.teamapp.core.db.dao.PendingActionDao_Impl;
import com.teamapp.core.db.dao.ProjectDao;
import com.teamapp.core.db.dao.ProjectDao_Impl;
import com.teamapp.core.db.dao.ProjectMemberDao;
import com.teamapp.core.db.dao.ProjectMemberDao_Impl;
import com.teamapp.core.db.dao.TaskAssigneeDao;
import com.teamapp.core.db.dao.TaskAssigneeDao_Impl;
import com.teamapp.core.db.dao.TaskDao;
import com.teamapp.core.db.dao.TaskDao_Impl;
import com.teamapp.core.db.dao.UserDao;
import com.teamapp.core.db.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDb_Impl extends AppDb {
  private volatile TaskDao _taskDao;

  private volatile UserDao _userDao;

  private volatile TaskAssigneeDao _taskAssigneeDao;

  private volatile ConversationMemberDao _conversationMemberDao;

  private volatile ProjectMemberDao _projectMemberDao;

  private volatile JoinRequestDao _joinRequestDao;

  private volatile ProjectDao _projectDao;

  private volatile CommentDao _commentDao;

  private volatile ConversationDao _conversationDao;

  private volatile MessageDao _messageDao;

  private volatile NotificationDao _notificationDao;

  private volatile PendingActionDao _pendingActionDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`id` TEXT NOT NULL, `projectId` TEXT, `title` TEXT, `description` TEXT, `status` TEXT, `position` REAL NOT NULL, `dueDate` INTEGER, `updatedAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_projectId` ON `tasks` (`projectId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_dueDate` ON `tasks` (`dueDate`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_tasks_updatedAt` ON `tasks` (`updatedAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `task_assignees` (`taskId` TEXT NOT NULL, `userId` TEXT NOT NULL, PRIMARY KEY(`taskId`, `userId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_task_assignees_taskId` ON `task_assignees` (`taskId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_task_assignees_userId` ON `task_assignees` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `projects` (`id` TEXT NOT NULL, `name` TEXT, `description` TEXT, `isPublic` INTEGER NOT NULL, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_createdAt` ON `projects` (`createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `project_members` (`projectId` TEXT NOT NULL, `userId` TEXT NOT NULL, `role` TEXT, PRIMARY KEY(`projectId`, `userId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_project_members_projectId` ON `project_members` (`projectId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_project_members_userId` ON `project_members` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `join_requests` (`id` TEXT NOT NULL, `projectId` TEXT, `requesterId` TEXT, `status` TEXT, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_join_requests_projectId` ON `join_requests` (`projectId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_join_requests_requesterId` ON `join_requests` (`requesterId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `conversation_members` (`conversationId` TEXT NOT NULL, `userId` TEXT NOT NULL, `lastReadMessageId` TEXT, `lastReadAt` INTEGER, PRIMARY KEY(`conversationId`, `userId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_conversation_members_conversationId` ON `conversation_members` (`conversationId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_conversation_members_userId` ON `conversation_members` (`userId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`id` TEXT NOT NULL, `taskId` TEXT, `authorId` TEXT, `authorName` TEXT, `content` TEXT, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_taskId` ON `comments` (`taskId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_createdAt` ON `comments` (`createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `conversations` (`id` TEXT NOT NULL, `title` TEXT, `type` TEXT, `lastMessageAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_conversations_lastMessageAt` ON `conversations` (`lastMessageAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `messages` (`id` TEXT NOT NULL, `conversationId` TEXT, `senderId` TEXT, `senderName` TEXT, `body` TEXT, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_messages_conversationId_createdAt` ON `messages` (`conversationId`, `createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notifications` (`id` TEXT NOT NULL, `type` TEXT, `dataJson` TEXT, `isRead` INTEGER NOT NULL, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_isRead` ON `notifications` (`isRead`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notifications_createdAt` ON `notifications` (`createdAt`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pending_actions` (`id` TEXT NOT NULL, `actionType` TEXT, `payloadJson` TEXT, `retryCount` INTEGER NOT NULL, `createdAt` INTEGER, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` TEXT NOT NULL, `email` TEXT, `fullName` TEXT, `avatarUrl` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '9ed11b751af366532a9e69f49b3ec5e4')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `tasks`");
        db.execSQL("DROP TABLE IF EXISTS `task_assignees`");
        db.execSQL("DROP TABLE IF EXISTS `projects`");
        db.execSQL("DROP TABLE IF EXISTS `project_members`");
        db.execSQL("DROP TABLE IF EXISTS `join_requests`");
        db.execSQL("DROP TABLE IF EXISTS `conversation_members`");
        db.execSQL("DROP TABLE IF EXISTS `comments`");
        db.execSQL("DROP TABLE IF EXISTS `conversations`");
        db.execSQL("DROP TABLE IF EXISTS `messages`");
        db.execSQL("DROP TABLE IF EXISTS `notifications`");
        db.execSQL("DROP TABLE IF EXISTS `pending_actions`");
        db.execSQL("DROP TABLE IF EXISTS `users`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsTasks = new HashMap<String, TableInfo.Column>(8);
        _columnsTasks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("projectId", new TableInfo.Column("projectId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("position", new TableInfo.Column("position", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("dueDate", new TableInfo.Column("dueDate", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTasks.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTasks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTasks = new HashSet<TableInfo.Index>(3);
        _indicesTasks.add(new TableInfo.Index("index_tasks_projectId", false, Arrays.asList("projectId"), Arrays.asList("ASC")));
        _indicesTasks.add(new TableInfo.Index("index_tasks_dueDate", false, Arrays.asList("dueDate"), Arrays.asList("ASC")));
        _indicesTasks.add(new TableInfo.Index("index_tasks_updatedAt", false, Arrays.asList("updatedAt"), Arrays.asList("ASC")));
        final TableInfo _infoTasks = new TableInfo("tasks", _columnsTasks, _foreignKeysTasks, _indicesTasks);
        final TableInfo _existingTasks = TableInfo.read(db, "tasks");
        if (!_infoTasks.equals(_existingTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "tasks(com.teamapp.data.entity.TaskEntity).\n"
                  + " Expected:\n" + _infoTasks + "\n"
                  + " Found:\n" + _existingTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsTaskAssignees = new HashMap<String, TableInfo.Column>(2);
        _columnsTaskAssignees.put("taskId", new TableInfo.Column("taskId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTaskAssignees.put("userId", new TableInfo.Column("userId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTaskAssignees = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTaskAssignees = new HashSet<TableInfo.Index>(2);
        _indicesTaskAssignees.add(new TableInfo.Index("index_task_assignees_taskId", false, Arrays.asList("taskId"), Arrays.asList("ASC")));
        _indicesTaskAssignees.add(new TableInfo.Index("index_task_assignees_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoTaskAssignees = new TableInfo("task_assignees", _columnsTaskAssignees, _foreignKeysTaskAssignees, _indicesTaskAssignees);
        final TableInfo _existingTaskAssignees = TableInfo.read(db, "task_assignees");
        if (!_infoTaskAssignees.equals(_existingTaskAssignees)) {
          return new RoomOpenHelper.ValidationResult(false, "task_assignees(com.teamapp.data.entity.TaskAssigneeEntity).\n"
                  + " Expected:\n" + _infoTaskAssignees + "\n"
                  + " Found:\n" + _existingTaskAssignees);
        }
        final HashMap<String, TableInfo.Column> _columnsProjects = new HashMap<String, TableInfo.Column>(5);
        _columnsProjects.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("isPublic", new TableInfo.Column("isPublic", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjects.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProjects = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProjects = new HashSet<TableInfo.Index>(1);
        _indicesProjects.add(new TableInfo.Index("index_projects_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoProjects = new TableInfo("projects", _columnsProjects, _foreignKeysProjects, _indicesProjects);
        final TableInfo _existingProjects = TableInfo.read(db, "projects");
        if (!_infoProjects.equals(_existingProjects)) {
          return new RoomOpenHelper.ValidationResult(false, "projects(com.teamapp.data.entity.ProjectEntity).\n"
                  + " Expected:\n" + _infoProjects + "\n"
                  + " Found:\n" + _existingProjects);
        }
        final HashMap<String, TableInfo.Column> _columnsProjectMembers = new HashMap<String, TableInfo.Column>(3);
        _columnsProjectMembers.put("projectId", new TableInfo.Column("projectId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMembers.put("userId", new TableInfo.Column("userId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProjectMembers.put("role", new TableInfo.Column("role", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProjectMembers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProjectMembers = new HashSet<TableInfo.Index>(2);
        _indicesProjectMembers.add(new TableInfo.Index("index_project_members_projectId", false, Arrays.asList("projectId"), Arrays.asList("ASC")));
        _indicesProjectMembers.add(new TableInfo.Index("index_project_members_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoProjectMembers = new TableInfo("project_members", _columnsProjectMembers, _foreignKeysProjectMembers, _indicesProjectMembers);
        final TableInfo _existingProjectMembers = TableInfo.read(db, "project_members");
        if (!_infoProjectMembers.equals(_existingProjectMembers)) {
          return new RoomOpenHelper.ValidationResult(false, "project_members(com.teamapp.data.entity.ProjectMemberEntity).\n"
                  + " Expected:\n" + _infoProjectMembers + "\n"
                  + " Found:\n" + _existingProjectMembers);
        }
        final HashMap<String, TableInfo.Column> _columnsJoinRequests = new HashMap<String, TableInfo.Column>(5);
        _columnsJoinRequests.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJoinRequests.put("projectId", new TableInfo.Column("projectId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJoinRequests.put("requesterId", new TableInfo.Column("requesterId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJoinRequests.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJoinRequests.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJoinRequests = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJoinRequests = new HashSet<TableInfo.Index>(2);
        _indicesJoinRequests.add(new TableInfo.Index("index_join_requests_projectId", false, Arrays.asList("projectId"), Arrays.asList("ASC")));
        _indicesJoinRequests.add(new TableInfo.Index("index_join_requests_requesterId", false, Arrays.asList("requesterId"), Arrays.asList("ASC")));
        final TableInfo _infoJoinRequests = new TableInfo("join_requests", _columnsJoinRequests, _foreignKeysJoinRequests, _indicesJoinRequests);
        final TableInfo _existingJoinRequests = TableInfo.read(db, "join_requests");
        if (!_infoJoinRequests.equals(_existingJoinRequests)) {
          return new RoomOpenHelper.ValidationResult(false, "join_requests(com.teamapp.data.entity.JoinRequestEntity).\n"
                  + " Expected:\n" + _infoJoinRequests + "\n"
                  + " Found:\n" + _existingJoinRequests);
        }
        final HashMap<String, TableInfo.Column> _columnsConversationMembers = new HashMap<String, TableInfo.Column>(4);
        _columnsConversationMembers.put("conversationId", new TableInfo.Column("conversationId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversationMembers.put("userId", new TableInfo.Column("userId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversationMembers.put("lastReadMessageId", new TableInfo.Column("lastReadMessageId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversationMembers.put("lastReadAt", new TableInfo.Column("lastReadAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysConversationMembers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesConversationMembers = new HashSet<TableInfo.Index>(2);
        _indicesConversationMembers.add(new TableInfo.Index("index_conversation_members_conversationId", false, Arrays.asList("conversationId"), Arrays.asList("ASC")));
        _indicesConversationMembers.add(new TableInfo.Index("index_conversation_members_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        final TableInfo _infoConversationMembers = new TableInfo("conversation_members", _columnsConversationMembers, _foreignKeysConversationMembers, _indicesConversationMembers);
        final TableInfo _existingConversationMembers = TableInfo.read(db, "conversation_members");
        if (!_infoConversationMembers.equals(_existingConversationMembers)) {
          return new RoomOpenHelper.ValidationResult(false, "conversation_members(com.teamapp.data.entity.ConversationMemberEntity).\n"
                  + " Expected:\n" + _infoConversationMembers + "\n"
                  + " Found:\n" + _existingConversationMembers);
        }
        final HashMap<String, TableInfo.Column> _columnsComments = new HashMap<String, TableInfo.Column>(6);
        _columnsComments.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("taskId", new TableInfo.Column("taskId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("authorId", new TableInfo.Column("authorId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("authorName", new TableInfo.Column("authorName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("content", new TableInfo.Column("content", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsComments.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysComments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesComments = new HashSet<TableInfo.Index>(2);
        _indicesComments.add(new TableInfo.Index("index_comments_taskId", false, Arrays.asList("taskId"), Arrays.asList("ASC")));
        _indicesComments.add(new TableInfo.Index("index_comments_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoComments = new TableInfo("comments", _columnsComments, _foreignKeysComments, _indicesComments);
        final TableInfo _existingComments = TableInfo.read(db, "comments");
        if (!_infoComments.equals(_existingComments)) {
          return new RoomOpenHelper.ValidationResult(false, "comments(com.teamapp.data.entity.CommentEntity).\n"
                  + " Expected:\n" + _infoComments + "\n"
                  + " Found:\n" + _existingComments);
        }
        final HashMap<String, TableInfo.Column> _columnsConversations = new HashMap<String, TableInfo.Column>(4);
        _columnsConversations.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversations.put("title", new TableInfo.Column("title", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversations.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsConversations.put("lastMessageAt", new TableInfo.Column("lastMessageAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysConversations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesConversations = new HashSet<TableInfo.Index>(1);
        _indicesConversations.add(new TableInfo.Index("index_conversations_lastMessageAt", false, Arrays.asList("lastMessageAt"), Arrays.asList("ASC")));
        final TableInfo _infoConversations = new TableInfo("conversations", _columnsConversations, _foreignKeysConversations, _indicesConversations);
        final TableInfo _existingConversations = TableInfo.read(db, "conversations");
        if (!_infoConversations.equals(_existingConversations)) {
          return new RoomOpenHelper.ValidationResult(false, "conversations(com.teamapp.data.entity.ConversationEntity).\n"
                  + " Expected:\n" + _infoConversations + "\n"
                  + " Found:\n" + _existingConversations);
        }
        final HashMap<String, TableInfo.Column> _columnsMessages = new HashMap<String, TableInfo.Column>(6);
        _columnsMessages.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("conversationId", new TableInfo.Column("conversationId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderId", new TableInfo.Column("senderId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("senderName", new TableInfo.Column("senderName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("body", new TableInfo.Column("body", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessages.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessages = new HashSet<TableInfo.Index>(1);
        _indicesMessages.add(new TableInfo.Index("index_messages_conversationId_createdAt", false, Arrays.asList("conversationId", "createdAt"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoMessages = new TableInfo("messages", _columnsMessages, _foreignKeysMessages, _indicesMessages);
        final TableInfo _existingMessages = TableInfo.read(db, "messages");
        if (!_infoMessages.equals(_existingMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "messages(com.teamapp.data.entity.MessageEntity).\n"
                  + " Expected:\n" + _infoMessages + "\n"
                  + " Found:\n" + _existingMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsNotifications = new HashMap<String, TableInfo.Column>(5);
        _columnsNotifications.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("dataJson", new TableInfo.Column("dataJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotifications.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotifications = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNotifications = new HashSet<TableInfo.Index>(2);
        _indicesNotifications.add(new TableInfo.Index("index_notifications_isRead", false, Arrays.asList("isRead"), Arrays.asList("ASC")));
        _indicesNotifications.add(new TableInfo.Index("index_notifications_createdAt", false, Arrays.asList("createdAt"), Arrays.asList("ASC")));
        final TableInfo _infoNotifications = new TableInfo("notifications", _columnsNotifications, _foreignKeysNotifications, _indicesNotifications);
        final TableInfo _existingNotifications = TableInfo.read(db, "notifications");
        if (!_infoNotifications.equals(_existingNotifications)) {
          return new RoomOpenHelper.ValidationResult(false, "notifications(com.teamapp.data.entity.NotificationEntity).\n"
                  + " Expected:\n" + _infoNotifications + "\n"
                  + " Found:\n" + _existingNotifications);
        }
        final HashMap<String, TableInfo.Column> _columnsPendingActions = new HashMap<String, TableInfo.Column>(5);
        _columnsPendingActions.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingActions.put("actionType", new TableInfo.Column("actionType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingActions.put("payloadJson", new TableInfo.Column("payloadJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingActions.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPendingActions.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPendingActions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPendingActions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPendingActions = new TableInfo("pending_actions", _columnsPendingActions, _foreignKeysPendingActions, _indicesPendingActions);
        final TableInfo _existingPendingActions = TableInfo.read(db, "pending_actions");
        if (!_infoPendingActions.equals(_existingPendingActions)) {
          return new RoomOpenHelper.ValidationResult(false, "pending_actions(com.teamapp.data.entity.PendingActionEntity).\n"
                  + " Expected:\n" + _infoPendingActions + "\n"
                  + " Found:\n" + _existingPendingActions);
        }
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(4);
        _columnsUsers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("fullName", new TableInfo.Column("fullName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("avatarUrl", new TableInfo.Column("avatarUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.teamapp.data.entity.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "9ed11b751af366532a9e69f49b3ec5e4", "2f014674cfef2ae93266c4ec8cea6615");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "tasks","task_assignees","projects","project_members","join_requests","conversation_members","comments","conversations","messages","notifications","pending_actions","users");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `tasks`");
      _db.execSQL("DELETE FROM `task_assignees`");
      _db.execSQL("DELETE FROM `projects`");
      _db.execSQL("DELETE FROM `project_members`");
      _db.execSQL("DELETE FROM `join_requests`");
      _db.execSQL("DELETE FROM `conversation_members`");
      _db.execSQL("DELETE FROM `comments`");
      _db.execSQL("DELETE FROM `conversations`");
      _db.execSQL("DELETE FROM `messages`");
      _db.execSQL("DELETE FROM `notifications`");
      _db.execSQL("DELETE FROM `pending_actions`");
      _db.execSQL("DELETE FROM `users`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(TaskDao.class, TaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TaskAssigneeDao.class, TaskAssigneeDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ConversationMemberDao.class, ConversationMemberDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProjectMemberDao.class, ProjectMemberDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(JoinRequestDao.class, JoinRequestDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProjectDao.class, ProjectDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CommentDao.class, CommentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ConversationDao.class, ConversationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MessageDao.class, MessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NotificationDao.class, NotificationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PendingActionDao.class, PendingActionDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public TaskDao taskDao() {
    if (_taskDao != null) {
      return _taskDao;
    } else {
      synchronized(this) {
        if(_taskDao == null) {
          _taskDao = new TaskDao_Impl(this);
        }
        return _taskDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public TaskAssigneeDao taskAssigneeDao() {
    if (_taskAssigneeDao != null) {
      return _taskAssigneeDao;
    } else {
      synchronized(this) {
        if(_taskAssigneeDao == null) {
          _taskAssigneeDao = new TaskAssigneeDao_Impl(this);
        }
        return _taskAssigneeDao;
      }
    }
  }

  @Override
  public ConversationMemberDao conversationMemberDao() {
    if (_conversationMemberDao != null) {
      return _conversationMemberDao;
    } else {
      synchronized(this) {
        if(_conversationMemberDao == null) {
          _conversationMemberDao = new ConversationMemberDao_Impl(this);
        }
        return _conversationMemberDao;
      }
    }
  }

  @Override
  public ProjectMemberDao projectMemberDao() {
    if (_projectMemberDao != null) {
      return _projectMemberDao;
    } else {
      synchronized(this) {
        if(_projectMemberDao == null) {
          _projectMemberDao = new ProjectMemberDao_Impl(this);
        }
        return _projectMemberDao;
      }
    }
  }

  @Override
  public JoinRequestDao joinRequestDao() {
    if (_joinRequestDao != null) {
      return _joinRequestDao;
    } else {
      synchronized(this) {
        if(_joinRequestDao == null) {
          _joinRequestDao = new JoinRequestDao_Impl(this);
        }
        return _joinRequestDao;
      }
    }
  }

  @Override
  public ProjectDao projectDao() {
    if (_projectDao != null) {
      return _projectDao;
    } else {
      synchronized(this) {
        if(_projectDao == null) {
          _projectDao = new ProjectDao_Impl(this);
        }
        return _projectDao;
      }
    }
  }

  @Override
  public CommentDao commentDao() {
    if (_commentDao != null) {
      return _commentDao;
    } else {
      synchronized(this) {
        if(_commentDao == null) {
          _commentDao = new CommentDao_Impl(this);
        }
        return _commentDao;
      }
    }
  }

  @Override
  public ConversationDao conversationDao() {
    if (_conversationDao != null) {
      return _conversationDao;
    } else {
      synchronized(this) {
        if(_conversationDao == null) {
          _conversationDao = new ConversationDao_Impl(this);
        }
        return _conversationDao;
      }
    }
  }

  @Override
  public MessageDao messageDao() {
    if (_messageDao != null) {
      return _messageDao;
    } else {
      synchronized(this) {
        if(_messageDao == null) {
          _messageDao = new MessageDao_Impl(this);
        }
        return _messageDao;
      }
    }
  }

  @Override
  public NotificationDao notificationDao() {
    if (_notificationDao != null) {
      return _notificationDao;
    } else {
      synchronized(this) {
        if(_notificationDao == null) {
          _notificationDao = new NotificationDao_Impl(this);
        }
        return _notificationDao;
      }
    }
  }

  @Override
  public PendingActionDao pendingActionDao() {
    if (_pendingActionDao != null) {
      return _pendingActionDao;
    } else {
      synchronized(this) {
        if(_pendingActionDao == null) {
          _pendingActionDao = new PendingActionDao_Impl(this);
        }
        return _pendingActionDao;
      }
    }
  }
}
