package com.hana4.ggumtle.model.entity.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PostRepositoryTest {
	@Autowired
	private PostRepository postRepository;

}