using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/tasks/{taskId:guid}/comments")]
[Authorize]
public class CommentsController : ControllerBase
{
    private readonly CommentService _svc;
    public CommentsController(CommentService svc) { _svc = svc; }

    [HttpGet]
    public Task<List<CommentDto>> List(Guid taskId, [FromQuery] int page = 1, [FromQuery] int pageSize = 50)
        => _svc.ListAsync(HttpContext.UserId(), taskId, page, pageSize);

    [HttpPost]
    public Task<CommentDto> Add(Guid taskId, [FromBody] AddCommentRequest req)
        => _svc.AddAsync(HttpContext.UserId(), taskId, req.Content);
}

[ApiController, Route("api/comments")]
[Authorize]
public class CommentAdminController : ControllerBase
{
    private readonly CommentService _svc;
    public CommentAdminController(CommentService svc) { _svc = svc; }

    [HttpDelete("{commentId:guid}")]
    public async Task<IActionResult> Delete(Guid commentId)
    {
        await _svc.DeleteAsync(HttpContext.UserId(), commentId);
        return NoContent();
    }
}
