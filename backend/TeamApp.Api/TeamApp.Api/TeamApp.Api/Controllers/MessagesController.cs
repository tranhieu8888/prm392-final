using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/conversations/{conversationId:guid}/messages")]
[Authorize]
public class MessagesController : ControllerBase
{
    private readonly MessageService _svc;
    public MessagesController(MessageService svc) { _svc = svc; }

    [HttpGet]
    public Task<List<MessageDto>> List(Guid conversationId, [FromQuery] DateTime? before = null, [FromQuery] int pageSize = 50)
        => _svc.ListAsync(HttpContext.UserId(), conversationId, before, pageSize);

    [HttpPost]
    public Task<MessageDto> Send(Guid conversationId, [FromBody] SendMessageRequest req)
        => _svc.SendAsync(HttpContext.UserId(), conversationId, req.Body);
}
