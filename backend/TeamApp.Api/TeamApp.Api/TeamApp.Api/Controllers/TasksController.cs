using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api")]
public class TasksController : ControllerBase
{
    private readonly TaskService _svc;
    public TasksController(TaskService svc) { _svc = svc; }

    [HttpGet("tasks/my"), Authorize]
    public Task<List<TaskDto>> My([FromQuery] string? status, [FromQuery] int page = 1, [FromQuery] int pageSize = 50)
        => _svc.MyTasksAsync(HttpContext.UserId(), status, page, pageSize);

    [HttpGet("projects/{projectId:guid}/tasks"), Authorize]
    public Task<List<TaskDto>> ByProject(Guid projectId) => _svc.ByProjectAsync(projectId);

    [HttpPost("projects/{projectId:guid}/tasks"), Authorize]
    public Task<TaskDto> Create(Guid projectId, [FromBody] CreateTaskRequest req)
        => _svc.CreateAsync(HttpContext.UserId(), projectId, req);

    [HttpPatch("tasks/{taskId:guid}/status"), Authorize]
    public async Task<IActionResult> UpdateStatus(Guid taskId, [FromBody] UpdateTaskStatusRequest req)
    {
        await _svc.UpdateStatusAsync(HttpContext.UserId(), taskId, req.Status, req.Position);
        return NoContent();
    }
}
