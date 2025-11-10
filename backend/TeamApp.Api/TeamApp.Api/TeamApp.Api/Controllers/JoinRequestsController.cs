// TeamApp.Api/Controllers/JoinRequestsController.cs
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils; // HttpContext.UserId()

namespace TeamApp.Api.Controllers;

[ApiController]
[Route("api/join-requests")]
[Authorize]
public class JoinRequestsController : ControllerBase
{
    private readonly JoinRequestService _svc;
    public JoinRequestsController(JoinRequestService svc) { _svc = svc; }

    // Gửi yêu cầu tham gia
    [HttpPost("{projectId:guid}")]
    public Task<JoinRequestDto> Request(Guid projectId) =>
        _svc.RequestAsync(HttpContext.UserId(), projectId);

    // Duyệt/Từ chối
    [HttpPost("{joinRequestId:guid}/decision")]
    public async Task<IActionResult> Decide(Guid joinRequestId, [FromBody] ApproveJoinRequestRequest req)
    {
        await _svc.ApproveAsync(HttpContext.UserId(), joinRequestId, req.Approve);
        return NoContent();
    }

    // ========= NEW: GET =========

    /// <summary>
    /// Tôi xem các yêu cầu mình đã gửi.
    /// GET /api/join-requests/my?status=PENDING&page=1&pageSize=20
    /// </summary>
    [HttpGet("my")]
    public Task<PagedResult<JoinRequestDto>> My(
        [FromQuery] string? status,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20)
        => _svc.ListMineAsync(HttpContext.UserId(), status, Normalize(page), NormalizeSize(pageSize));

    /// <summary>
    /// Manager xem yêu cầu vào một project.
    /// GET /api/join-requests/project/{projectId}?status=PENDING&page=1&pageSize=20
    /// </summary>
    [HttpGet("project/{projectId:guid}")]
    public Task<PagedResult<JoinRequestDto>> ForProject(
        Guid projectId,
        [FromQuery] string? status,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20)
        => _svc.ListForProjectAsync(HttpContext.UserId(), projectId, status, Normalize(page), NormalizeSize(pageSize));

    /// <summary>
    /// Xem chi tiết 1 yêu cầu (requester hoặc manager của project).
    /// </summary>
    [HttpGet("{joinRequestId:guid}")]
    public Task<JoinRequestDto> Get(Guid joinRequestId) =>
        _svc.GetAsync(HttpContext.UserId(), joinRequestId);

    private static int Normalize(int page) => page <= 0 ? 1 : page;
    private static int NormalizeSize(int size) => size <= 0 ? 20 : Math.Min(size, 100);
}
