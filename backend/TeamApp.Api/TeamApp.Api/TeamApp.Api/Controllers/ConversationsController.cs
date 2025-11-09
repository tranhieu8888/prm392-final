using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/conversations")]
[Authorize]
public class ConversationsController : ControllerBase
{
    private readonly ConversationService _svc;
    public ConversationsController(ConversationService svc) { _svc = svc; }

    [HttpGet("my")]
    public Task<List<ConversationDto>> My([FromQuery] int page = 1, [FromQuery] int pageSize = 50)
        => _svc.MyConversationsAsync(HttpContext.UserId(), page, pageSize);

    [HttpPost("dm")]
    public Task<Guid> StartDm([FromBody] StartDmRequest req)
        => _svc.StartDmAsync(HttpContext.UserId(), req.OtherUserId);

    [HttpPost("group")]
    public Task<Guid> CreateGroup([FromBody] CreateGroupRequest req)
        => _svc.CreateGroupAsync(HttpContext.UserId(), req.ProjectId, req.Title, req.MemberIds);
}
