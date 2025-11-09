using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

using TeamApp.Application;

using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/notifications")]
public class NotificationsController : ControllerBase
{
    private readonly NotificationService _svc;
    public NotificationsController(NotificationService svc) { _svc = svc; }

    [HttpGet, Authorize]
    public Task<List<NotificationDto>> List() => _svc.ListAsync(HttpContext.UserId());

    [HttpPatch("{id:guid}"), Authorize]
    public async Task<IActionResult> Mark(Guid id, MarkReadRequest req)
    {
        await _svc.MarkAsync(HttpContext.UserId(), id, req.IsRead);
        return NoContent();
    }
}
