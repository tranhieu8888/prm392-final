using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TeamApp.Domain
{
    public enum ProjectRole { Manager, Member }

    public enum TaskStatus { TODO, IN_PROGRESS, IN_REVIEW, DONE }
    public enum ProjectStatus { TODO, IN_PROGRESS, IN_REVIEW, DONE }
    public enum ConversationType { Group, DM }

    public enum JoinStatus { PENDING, APPROVED, REJECTED }

    /// <summary>
    /// Tùy dự án bạn có thể để string thay vì enum để linh hoạt hơn.
    /// </summary>
    public enum NotificationType
    {
        TASK_ASSIGNED,
        TASK_STATUS_CHANGED,
        TASK_COMMENTED,
        JOIN_REQUEST,
        JOIN_APPROVED,
        GENERIC
    }
}
