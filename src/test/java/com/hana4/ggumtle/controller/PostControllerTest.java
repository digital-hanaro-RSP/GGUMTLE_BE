package com.hana4.ggumtle.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hana4.ggumtle.WithMockCustomUser;
import com.hana4.ggumtle.config.TestSecurityConfig;
import com.hana4.ggumtle.dto.advertisement.AdvertisementResponseDto;
import com.hana4.ggumtle.dto.post.PostLikeResponseDto;
import com.hana4.ggumtle.dto.post.PostRequestDto;
import com.hana4.ggumtle.dto.post.PostResponseDto;
import com.hana4.ggumtle.dto.user.UserResponseDto;
import com.hana4.ggumtle.model.entity.advertisement.Advertisement;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementAdType;
import com.hana4.ggumtle.model.entity.advertisement.AdvertisementLocationType;
import com.hana4.ggumtle.model.entity.group.GroupCategory;
import com.hana4.ggumtle.model.entity.post.Post;
import com.hana4.ggumtle.model.entity.post.PostType;
import com.hana4.ggumtle.security.CustomUserDetails;
import com.hana4.ggumtle.service.AdvertisementService;
import com.hana4.ggumtle.service.PostService;

@WebMvcTest(controllers = PostController.class,
	excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {OncePerRequestFilter.class})
	})
@WithMockCustomUser
@Import(TestSecurityConfig.class)
class PostControllerTest {

	@MockitoBean
	PostService postService;

	@MockitoBean
	AdvertisementService advertisementService;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	WebApplicationContext webApplicationContext;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.apply(springSecurity()) // Spring Security 통합
			.build();
	}

	@Test
	void writePost() throws Exception {

		// given
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		PostRequestDto.Write write = new PostRequestDto.Write(imageUrls, content, snapShot, PostType.POST);

		PostResponseDto.PostInfo post = new PostResponseDto.PostInfo(
			1L,
			"1", // userId
			1L, // groupId
			null, // snapShot
			imageUrls, // imageUrls
			content, // content
			PostType.POST, // postType
			UserResponseDto.BriefInfo.from(customUserDetails.getUser()),
			GroupCategory.AFTER_RETIREMENT,
			false,
			true,
			0, 0
		);

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.save(eq(1L), eq(write), eq(customUserDetails.getUser()))).willReturn(post);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/group/{groupId}/post", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(post.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(post.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(post.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(post.getContent()))
			.andExpect(jsonPath("$.data.postType").value(post.getPostType().name()))
			.andDo(print());

		verify(postService).save(1L, write, customUserDetails.getUser());
	}

	@Test
	void getPost() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.userBriefInfo(UserResponseDto.BriefInfo.from(customUserDetails.getUser()))
			.likeCount(1)
			.commentCount(1)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPost(eq(groupId), eq(postId), eq(customUserDetails.getUser()))).willReturn(postInfo);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/post/{postId}", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(postInfo.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(postInfo.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(postInfo.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(postInfo.getContent()))
			.andExpect(jsonPath("$.data.postType").value(postInfo.getPostType().name()))
			.andDo(print());

		verify(postService).getPost(groupId, postId, customUserDetails.getUser());
	}

	@Test
	void getPostsByPage() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<PostResponseDto.PostInfo> postInfos = new ArrayList<>();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		for (int i = 0; i < 10; i++)
			postInfos.add(postInfo);

		Page<PostResponseDto.PostInfo> pages = new PageImpl<>(postInfos, pageable, postInfos.size());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPostsByPage(eq(groupId), eq(pageable), eq(customUserDetails.getUser()))
		).willReturn(pages);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/post", groupId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.content[0].userId").value(postInfos.get(0).getUserId()))
			.andExpect(jsonPath("$.data.content[1].userId").value(postInfos.get(1).getUserId()))
			.andExpect(jsonPath("$.data.content[2].userId").value(postInfos.get(2).getUserId()))
			.andExpect(jsonPath("$.data.content[3].userId").value(postInfos.get(3).getUserId()))
			.andExpect(jsonPath("$.data.content[4].userId").value(postInfos.get(4).getUserId()))
			.andExpect(jsonPath("$.data.content[5].userId").value(postInfos.get(5).getUserId()))
			.andExpect(jsonPath("$.data.content[6].userId").value(postInfos.get(6).getUserId()))
			.andExpect(jsonPath("$.data.content[7].userId").value(postInfos.get(7).getUserId()))
			.andExpect(jsonPath("$.data.content[8].userId").value(postInfos.get(8).getUserId()))
			.andExpect(jsonPath("$.data.content[9].userId").value(postInfos.get(9).getUserId()))
			.andDo(print());

		verify(postService).getPostsByPage(groupId, pageable, customUserDetails.getUser());
	}

	@Test
	void getPostsByPage_실패() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<PostResponseDto.PostInfo> postInfos = new ArrayList<>();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		for (int i = 0; i < 10; i++)
			postInfos.add(postInfo);

		Page<PostResponseDto.PostInfo> pages = new PageImpl<>(postInfos, pageable, postInfos.size());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPostsByPage(eq(groupId), eq(pageable), eq(customUserDetails.getUser()))
		).willReturn(pages);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/post", 1).param("offset", "0")
				.param("limit", "-1"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.message").value("limit은 0 이상이어야 합니다."))
			.andDo(print());
	}

	@Test
	void updatePost() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;

		String imageUrls = "imageUrl";
		String content = "글 내용";
		String snapShot = "{\"bucketId\":[3,2,1],\"portfolio\":false}";

		PostRequestDto.Write write = PostRequestDto.Write.builder()
			.imageUrls(imageUrls)
			.content(content)
			.snapShot(snapShot)
			.build();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot(snapShot)
			.imageUrls(imageUrls)
			.content(content)
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.updatePost(eq(1L), eq(1L), eq(write), eq(customUserDetails.getUser()))).willReturn(postInfo);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.patch("/community/group/{groupId}/post/{postId}", groupId, postId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(postInfo.getUserId()))
			.andExpect(jsonPath("$.data.groupId").value(postInfo.getGroupId()))
			.andExpect(jsonPath("$.data.imageUrls").value(postInfo.getImageUrls()))
			.andExpect(jsonPath("$.data.content").value(postInfo.getContent()))
			.andExpect(jsonPath("$.data.postType").value(postInfo.getPostType().name()))
			.andDo(print());

		verify(postService).updatePost(groupId, postId, write, customUserDetails.getUser());
	}

	@Test
	void deletePost() throws Exception {
		// given
		Long groupId = 1L;
		Long postId = 1L;

		String imageUrls = "imageUrl";
		String content = "글 내용";
		String snapShot = "{\"bucketId\":[3,2,1],\"portfolio\":false}";

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.delete("/community/group/{groupId}/post/{postId}", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());

		verify(postService).deletePost(groupId, postId, customUserDetails.getUser());
	}

	@Test
	void getPopularPostsByPage() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<PostResponseDto.PostInfo> postInfos = new ArrayList<>();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		for (int i = 0; i < 10; i++)
			postInfos.add(postInfo);

		Page<PostResponseDto.PostInfo> pages = new PageImpl<>(postInfos, pageable, postInfos.size());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPopularPostsByPage(eq(pageable), eq(customUserDetails.getUser()), eq(null), eq(null))
		).willReturn(pages);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/post/popular"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andDo(print());

		verify(postService).getPopularPostsByPage(pageable, customUserDetails.getUser(), null, null);
	}

	@Test
	void getPopularPostsByPage_예외처리() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;
		Pageable pageable = PageRequest.of(0, 10);

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<PostResponseDto.PostInfo> postInfos = new ArrayList<>();

		PostResponseDto.PostInfo postInfo = PostResponseDto.PostInfo.builder()
			.id(postId)
			.userId("1")
			.groupId(groupId)
			.snapShot("{\"bucketId\":[3,2,1],\"portfolio\":false}")
			.imageUrls("")
			.content("글 내용")
			.postType(PostType.POST)
			.isLiked(false)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		for (int i = 0; i < 10; i++)
			postInfos.add(postInfo);

		Page<PostResponseDto.PostInfo> pages = new PageImpl<>(postInfos, pageable, postInfos.size());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.getPopularPostsByPage(eq(pageable), eq(customUserDetails.getUser()), eq(null), eq(null))
		).willReturn(pages);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/post/popular")
				.param("offset", "0").param("limit", "-1"))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(400))
			.andExpect(jsonPath("$.message").value("limit은 0 이상이어야 합니다."))
			.andDo(print());
	}

	@Test
	void likePost() throws Exception {

		// given
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		PostRequestDto.Write write = new PostRequestDto.Write(imageUrls, content, snapShot, PostType.POST);

		// PostResponseDto.PostInfo post = new PostResponseDto.PostInfo(
		// 	1L,
		// 	"1", // userId
		// 	1L, // groupId
		// 	null, // snapShot
		// 	imageUrls, // imageUrls
		// 	content, // content
		// 	PostType.POST, // postType
		// 	UserResponseDto.BriefInfo.from(customUserDetails.getUser()),
		// 	GroupCategory.AFTER_RETIREMENT,
		// 	false,
		// 	true,
		// 	0, 0
		// );

		Post post = new Post();
		post.setId(1L);

		PostLikeResponseDto.Add addLike = new PostLikeResponseDto.Add(1L, customUserDetails.getUser().getId(), 1L,
			LocalDateTime.now());
		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.addLike(eq(1L), eq(1L), eq(customUserDetails.getUser()))).willReturn(addLike);

		// when
		String reqBody = objectMapper.writeValueAsString(write);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/group/{groupId}/post/{postId}/like", 1L, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.userId").value(1L))
			.andExpect(jsonPath("$.data.postId").value(1L))
			.andDo(print());

		verify(postService).addLike(1L, 1L, customUserDetails.getUser());
	}

	@Test
	void dislikePost() throws Exception {
		// given
		Long groupId = 1L;
		Long postId = 1L;

		String imageUrls = "imageUrl";
		String content = "글 내용";
		String snapShot = "{\"bucketId\":[3,2,1],\"portfolio\":false}";

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		System.out.println("customUserDetails = " + customUserDetails.getUser());

		// when, then
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/community/group/{groupId}/post/{postId}/dislike", groupId, postId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data").doesNotExist())
			.andDo(print());

		verify(postService).removeLike(groupId, postId, customUserDetails.getUser());
	}

	@Test
	void sharePost() throws Exception {

		// given
		String imageUrls = "imageUrl";
		String content = "content";
		String snapShot = "snapShot";
		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		PostRequestDto.Share share = new PostRequestDto.Share(content, PostType.NEWS);

		PostResponseDto.ShareInfo post = new PostResponseDto.ShareInfo(
			1L,
			content, // content
			UserResponseDto.BriefInfo.from(customUserDetails.getUser()),
			PostType.NEWS // postType
		);

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(postService.saveNews(eq(1L), eq(share), eq(customUserDetails.getUser()))).willReturn(post);

		// when
		String reqBody = objectMapper.writeValueAsString(share);

		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/community/group/{groupId}/post/share", 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(reqBody))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(post.getId()))
			.andExpect(jsonPath("$.data.content").value(post.getContent()))
			.andExpect(jsonPath("$.data.briefInfo.name").value(post.getBriefInfo().getName()))
			.andExpect(jsonPath("$.data.postType").value(post.getPostType().name()))
			.andDo(print());

		verify(postService).saveNews(1L, share, customUserDetails.getUser());
	}

	@Test
	void getAdvertisement() throws Exception {

		// given
		Long groupId = 1L;
		Long postId = 1L;

		CustomUserDetails customUserDetails = (CustomUserDetails)SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		Advertisement community1 = Advertisement.builder()
			.locationType(AdvertisementLocationType.COMMUNITY)
			.adType(AdvertisementAdType.HANA)
			.bannerImageUrl(
				"https://ggumtlebucket.s3.ap-northeast-2.amazonaws.com/hanainvest.png")
			.link("https://www.hanaw.com/main/main/index.cmd")
			.build();

		AdvertisementResponseDto.CommunityAd communityAd = AdvertisementResponseDto.CommunityAd.from(community1);

		System.out.println("customUserDetails = " + customUserDetails.getUser());
		given(advertisementService.getCommunityAd(eq(groupId))).willReturn(communityAd);

		// when, then
		mockMvc.perform(MockMvcRequestBuilders.get("/community/group/{groupId}/advertisement", groupId))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.code").value(200))
			.andExpect(jsonPath("$.message").value("ok"))
			.andExpect(jsonPath("$.data.id").value(communityAd.getId()))
			.andExpect(jsonPath("$.data.locationType").value(communityAd.getLocationType().name()))
			.andExpect(jsonPath("$.data.adType").value(communityAd.getAdType().name()))
			.andExpect(jsonPath("$.data.bannerImageUrl").value(communityAd.getBannerImageUrl()))
			.andExpect(jsonPath("$.data.link").value(communityAd.getLink()))
			.andDo(print());

		verify(advertisementService).getCommunityAd(groupId);
	}
}
