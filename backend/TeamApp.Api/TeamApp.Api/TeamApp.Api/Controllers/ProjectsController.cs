using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/projects")]
public class ProjectsController : ControllerBase
{
    private readonly ProjectService _svc;
    public ProjectsController(ProjectService svc) { _svc = svc; }

    [HttpGet("my"), Authorize]
    public Task<List<ProjectDto>> My() => _svc.MyAsync(HttpContext.UserId());

    [HttpPost, Authorize]
    public Task<ProjectDto> Create([FromBody] CreateProjectRequest req)
        => _svc.CreateAsync(HttpContext.UserId(), req);

    [HttpGet("{id:guid}/members"), Authorize]
    public Task<List<MemberDto>> Members(Guid id) => _svc.MembersAsync(id);

    [HttpPost("{id:guid}/members"), Authorize]
    public async Task<IActionResult> Invite(Guid id, [FromBody] Dictionary<string, string> body)
    {
        if (!body.TryGetValue("email", out var email) || string.IsNullOrWhiteSpace(email))
            return BadRequest("Missing email");
        await _svc.InviteByEmailAsync(HttpContext.UserId(), id, email);
        return NoContent();
    }

    [HttpGet("discover"), Authorize]
    public Task<List<ProjectDto>> Discover([FromQuery] string? query)
        => _svc.DiscoverAsync(HttpContext.UserId(), query);
}
