package com.sound.service.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sound.constant.Constant;
import com.sound.exception.SoundException;
import com.sound.filter.authentication.ResourceAllowed;
import com.sound.model.RequestModel.commentRequest;
import com.sound.model.Sound;
import com.sound.model.SoundActivity.SoundComment;
import com.sound.model.SoundActivity.SoundLike;
import com.sound.model.SoundActivity.SoundRecord;
import com.sound.model.User;
import com.sound.service.sound.itf.SoundService;
import com.sound.service.sound.itf.SoundSocialService;

@Component
@Path("/soundActivity")
@RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE,
    Constant.GUEST_ROLE})
public class SoundSocialServiceEndpoint {

  Logger logger = Logger.getLogger(SoundSocialServiceEndpoint.class);

  @Autowired
  SoundSocialService soundSocialService;

  @Autowired
  SoundService soundService;

  @Autowired
  com.sound.service.user.itf.UserService userService;

  @Context
  HttpServletRequest req;

  @PUT
  @Path("/play/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceAllowed
  public Map<String, String> play(@NotNull @PathParam("soundId") String soundId) {
    Map<String, String> result = null;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      result = soundSocialService.play(currentUser, sound);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @PUT
  @Path("/like/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> like(@NotNull @PathParam("soundId") String soundId) {
    Integer liked = 0;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      liked = soundSocialService.like(currentUser, sound);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("liked", liked);
    return result;
  }

  @DELETE
  @Path("/like/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> unlike(@NotNull @PathParam("soundId") String soundId) {
    Integer liked = 0;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      liked = soundSocialService.dislike(currentUser, sound);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("liked", liked);
    return result;
  }

  @GET
  @Path("/{soundId}/likes")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceAllowed
  public List<SoundLike> likes(@NotNull @PathParam("soundId") String soundId,
      @NotNull @QueryParam("pageNum") Integer pageNum,
      @NotNull @QueryParam("perPage") Integer perPage) {
    List<SoundLike> likes = null;
    try {
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      likes = soundSocialService.getLiked(sound, pageNum, perPage);

      User curUser = userService.getCurrentUser(req);
      for (SoundLike like : likes) {
        like.getOwner().setUserPrefer(userService.getUserPrefer(curUser, like.getOwner()));
      }
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }

    return likes;
  }

  @PUT
  @Path("/repost/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> repost(@NotNull @PathParam("soundId") String soundId) {
    Integer reposted = 0;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      reposted = soundSocialService.repost(currentUser, sound);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("reposted", reposted);
    return result;
  }

  @DELETE
  @Path("/repost/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> unrepost(@NotNull @PathParam("soundId") String soundId) {
    Integer reposted = 0;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      reposted = soundSocialService.unrepost(currentUser, sound);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("reposted", reposted);
    return result;
  }

  @GET
  @Path("/{soundId}/reposts")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceAllowed
  public List<SoundRecord> reports(@NotNull @PathParam("soundId") String soundId,
      @NotNull @QueryParam("pageNum") Integer pageNum,
      @NotNull @QueryParam("perPage") Integer perPage) {
    List<SoundRecord> reports = null;
    try {
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      reports = soundSocialService.getReposts(sound, pageNum, perPage);

      User curUser = userService.getCurrentUser(req);
      for (SoundRecord report : reports) {
        report.getOwner().setUserPrefer(userService.getUserPrefer(curUser, report.getOwner()));
      }
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }

    return reports;
  }

  @PUT
  @Path("/comment/{soundId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> comment(@NotNull @PathParam("soundId") String soundId,
      @NotNull commentRequest request) {
    Integer commentsCount = 0;
    User currentUser = null;
    try {
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      if (null != sound.getProfile().getCommentMode()
          && sound.getProfile().getCommentMode().equals(Constant.COMMENT_CLOSED)) {
        throw new WebApplicationException(Status.FORBIDDEN);
      }

      currentUser = userService.getCurrentUser(req);
      User toUser = null;
      if (!StringUtils.isBlank(request.getToUserAlias())) {
        toUser = userService.getUserByAlias(request.getToUserAlias());
      }

      commentsCount =
          soundSocialService.comment(sound, currentUser, toUser, request.getComment(),
              request.getPointAt());
    } 
    catch (SoundException e)
    {
      throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build()); 
    }
    catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("commentsCount", commentsCount);
    return result;
  }

  @DELETE
  @Path("/comment/{soundId}/{commentId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Integer> comment(@NotNull @PathParam("soundId") String soundId,
      @NotNull @PathParam("commentId") String commentId) {
    Integer commentsCount = 0;
    try {
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      commentsCount = soundSocialService.uncomment(sound, commentId);
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("commentsCount", commentsCount);
    return result;
  }

  @GET
  @Path("/{soundId}/comments")
  @Produces(MediaType.APPLICATION_JSON)
  @ResourceAllowed
  public List<SoundComment> comment(@NotNull @PathParam("soundId") String soundId,
      @QueryParam("pageNum") Integer pageNum,
      @QueryParam("commentsPerPage") Integer commentsPerPage,
      @QueryParam("justInSound") String justInSound) {
    List<SoundComment> comments = null;
    try {
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      boolean commentsInSound = (null == justInSound) ? false : Boolean.parseBoolean(justInSound);
      if (commentsInSound) {
        comments = soundSocialService.getCommentsInsound(sound);
      } else {
        comments = soundSocialService.getComments(sound, pageNum, commentsPerPage);
      }
    } catch (SoundException e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }

    return comments;
  }

  @GET
  @Path("/recommand/sounds")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public List<Sound> getRecommandedSounds(@NotNull @QueryParam("pageNum") Integer pageNum,
      @NotNull @QueryParam("pageSize") Integer pageSize) {
    List<Sound> sounds = new ArrayList<Sound>();
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      sounds.addAll(soundSocialService.recommandSoundsForUser(currentUser, pageNum, pageSize));
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    return sounds;
  }

  @PUT
  @Path("/report/{soundId}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed({Constant.ADMIN_ROLE, Constant.PRO_ROLE, Constant.SPRO_ROLE, Constant.USER_ROLE})
  public Map<String, Boolean> report(@NotNull @PathParam("soundId") String soundId)
      throws SoundException {
    Boolean invalid = false;
    User currentUser = null;
    try {
      currentUser = userService.getCurrentUser(req);
      Sound sound = soundService.loadById(soundId);
      if (null == sound) {
        throw new WebApplicationException(Status.NOT_FOUND);
      }

      invalid = soundSocialService.report(currentUser, sound);
    } catch (Exception e) {
      logger.error(e);
      throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
    }
    Map<String, Boolean> result = new HashMap<String, Boolean>();
    result.put("invalid", invalid);
    return result;
  }
}
