using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

using TeamApp.Application;

using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/join-requests")]
public class JoinRequestsController : ControllerBase
{
    private readonly JoinRequestService _svc;
    public JoinRequestsController(JoinRequestService svc) { _svc = svc; }

    [HttpPost("{projectId:guid}"), Authorize]
    public Task<JoinRequestDto> Request(Guid projectId) => _svc.RequestAsync(HttpContext.UserId(), projectId);

    [HttpPost("{joinRequestId:guid}/decision"), Authorize]
    public async Task<IActionResult> Decide(Guid joinRequestId, ApproveJoinRequestRequest req)
    {
        await _svc.ApproveAsync(HttpContext.UserId(), joinRequestId, req.Approve);
        return NoContent();
    }
}
