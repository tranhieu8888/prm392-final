package com.teamapp.data.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskDtos {

    /**
     * Dùng để GỬI request TẠO task
     */
    public static class CreateTaskRequest {
        @SerializedName("title") public String title;
        @SerializedName("description") public String description;
        @SerializedName("dueDate") public Date dueDate;
        @SerializedName("assigneeIds") public List<UUID> assigneeIds;
        public CreateTaskRequest(String t, String d, Date due, List<UUID> ids) {
            title = t; description = d; dueDate = due; assigneeIds = ids;
        }
    }

    /**
     * Dùng để NHẬN dữ liệu Task (từ API)
     */
    public static class TaskDto {
        @SerializedName("id") public UUID id;
        @SerializedName("projectId") public UUID projectId;
        @SerializedName("title") public String title;
        @SerializedName("description") public String description;
        @SerializedName("status") public String status;

        // ===== SỬA LỖI CHÍNH NẰM Ở ĐÂY =====
        // Phải là 'Double' (viết hoa D) để có thể nhận giá trị 'null'
        // từ API (JSON) mà không làm crash ứng dụng.
        @SerializedName("position") public Double position;
        // ===== HẾT SỬA LỖI =====

        @SerializedName("dueDate") public Date dueDate;
        @SerializedName("updatedAt") public Date updatedAt;

        // Constructor rỗng mặc định được Java tự động thêm vào
        // Gson sẽ dùng constructor này khi parse JSON
    }

    /**
     * Dùng để GỬI request CẬP NHẬT status/position
     * (Sửa lại để khớp với cách gọi trong TaskRepository)
     */
    public static class UpdateTaskStatusRequest {

        @SerializedName("status")
        private String status;

        @SerializedName("position")
        private double position; // Kiểu 'double' ở đây là an toàn

        // Constructor rỗng (để TaskRepository gọi 'new TaskDtos.UpdateTaskStatusRequest()')
        public UpdateTaskStatusRequest(String status, double position) {
            this.status = status;
            this.position = position;
        }

        public UpdateTaskStatusRequest() {

        }


        // Setters (để TaskRepository gọi 'req.setStatus(...)' và 'req.setPosition(...)')
        public void setStatus(String status) {
            this.status = status;
        }

        public void setPosition(double position) {
            this.position = position;
        }

        // Getters (không bắt buộc nhưng nên có)
        public String getStatus() {
            return status;
        }

        public double getPosition() {
            return position;
        }
    }


}