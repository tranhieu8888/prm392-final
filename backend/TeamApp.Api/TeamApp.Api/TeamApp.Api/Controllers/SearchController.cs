using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/search")]
public class SearchController : ControllerBase
{
    private readonly SearchService _svc;
    public SearchController(SearchService svc) { _svc = svc; }

    [HttpGet, Authorize]
    public Task<SearchResultDto> Global([FromQuery] string q)
        => _svc.GlobalAsync(HttpContext.UserId(), q);
}
