CREATE TABLE `User` (
                        `id` CHAR(36) NOT NULL,
                        `tel` VARCHAR(255) NOT NULL,
                        `password` VARCHAR(255) NOT NULL,
                        `name` VARCHAR(255) NOT NULL COMMENT '실명',
                        `permission` SMALLINT NOT NULL DEFAULT 0 COMMENT '마이데이터 약관동의',
                        `birthDate` TIMESTAMP NOT NULL COMMENT 'YYYY.mm.dd',
                        `gender` BIGINT NOT NULL,
                        `role` ENUM('user', 'admin') NOT NULL DEFAULT 'user',
                        `profileImageUrl` VARCHAR(255) NULL,
                        `nickname` VARCHAR(255) NOT NULL,
                        `createdAt` TIMESTAMP NOT NULL,
                        `updatedAt` TIMESTAMP NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `user_tel_unique` (`tel`)
);
CREATE TABLE `Advertisement` (
                                 `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                 `productType` ENUM(
                                     'savingTimeDeposit',
                                     'investment',
                                     'foreignCurrency',
                                     'pension'
                                     ) NULL COMMENT '4가지에 대해서만 광고',
                                 `productName` VARCHAR(255) NULL,
                                 `bannerImageUrl` VARCHAR(255) NULL,
                                 `locationType` ENUM('Main', 'Community') NOT NULL COMMENT '메인화면의 광고인지, 커뮤니티에서의 광고인지',
                                 `security` VARCHAR(255) NULL,
                                 `riskRating` VARCHAR(255) NULL,
                                 `yield` VARCHAR(255) NULL COMMENT '수익률',
                                 `link` VARCHAR(255) NOT NULL
);

CREATE TABLE `DreamAccount` (
                                `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                `userId` CHAR(36) NOT NULL,
                                `balance` BIGINT NOT NULL COMMENT '현재 잔액',
                                `total` BIGINT NOT NULL COMMENT '전체 금액',
                                `createdAt` TIMESTAMP NOT NULL,
                                `updatedAt` TIMESTAMP NOT NULL,
                                CONSTRAINT `dreamaccount_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`)
);
CREATE TABLE `MyData` (
                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          `userId` CHAR(36) NOT NULL,
                          `depositWithdrawal` DECIMAL(8, 2) NOT NULL COMMENT '입출금',
                          `savingTimeDeposit` DECIMAL(8, 2) NOT NULL COMMENT '예적금',
                          `investment` DECIMAL(8, 2) NOT NULL COMMENT '투자',
                          `foreignCurrency` DECIMAL(8, 2) NOT NULL COMMENT '외화',
                          `pension` DECIMAL(8, 2) NOT NULL COMMENT '연금',
                          `etc` DECIMAL(8, 2) NOT NULL COMMENT '기타',
                          CONSTRAINT `mydata_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`)
);

CREATE TABLE `Bucket` (
                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          `dreamAccountId` BIGINT unsigned NOT NULL,
                          `userId` CHAR(36) NOT NULL,
                          `title` VARCHAR(255) NOT NULL,
                          `tagType` ENUM('Do', 'Be', 'Have', 'Go', 'Learn') NOT NULL COMMENT '태그 타입',
                          `dueDate` TIMESTAMP NULL,
                          `isDueSet` BOOLEAN NOT NULL DEFAULT 0,
                          `memo` TEXT NULL,
                          `howTo` ENUM('Money', 'Effort', 'Will') NULL COMMENT '방법',
                          `goalAmount` BIGINT NULL COMMENT '목표 금액',
                          `followers` BIGINT NULL,
                          `status` ENUM('Doing', 'Done', 'Hold') NOT NULL,
                          `isAutoAllocate` BOOLEAN NOT NULL DEFAULT 0 COMMENT '자동 분배',
                          `cronCycle` BIGINT NULL,
                          `safeBox` DECIMAL(8, 2) NULL,
                          `createdAt` TIMESTAMP NOT NULL,
                          `updatedAt` TIMESTAMP NOT NULL,
                          `isRecommended` BOOLEAN NOT NULL DEFAULT 0,
                          CONSTRAINT `bucket_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`),
                          CONSTRAINT `bucket_dreamaccountid_fk` FOREIGN KEY (`dreamAccountId`) REFERENCES `DreamAccount`(`id`)
);

CREATE TABLE `Survey` (
                          `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          `userId` CHAR(36) NOT NULL,
                          `answers` JSON NOT NULL,
                          `createdAt` TIMESTAMP NOT NULL,
                          `updatedAt` TIMESTAMP NOT NULL,
                          CONSTRAINT `survey_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`)
);

CREATE TABLE `Group` (
                         `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         `name` VARCHAR(255) NOT NULL,
                         `category` ENUM('Travel', 'Hobby', 'Finance', 'Study') NOT NULL COMMENT '카테고리',
                         `description` VARCHAR(255) NOT NULL,
                         `imageUrl` VARCHAR(255) NOT NULL,
                         `createdAt` TIMESTAMP NOT NULL,
                         `updatedAt` TIMESTAMP NOT NULL
);

CREATE TABLE `GroupMember` (
                               `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               `groupId` BIGINT unsigned NOT NULL,
                               `userId` CHAR(36) NOT NULL,
                               `createdAt` TIMESTAMP NOT NULL,
                               `updatedAt` TIMESTAMP NOT NULL,
                               CONSTRAINT `groupmember_groupid_userid_unique` UNIQUE (`groupId`, `userId`),
                               CONSTRAINT `groupmember_groupid_fk` FOREIGN KEY (`groupId`) REFERENCES `Group`(`id`),
                               CONSTRAINT `groupmember_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`)
);

CREATE TABLE `Post` (
                        `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        `userId` CHAR(36) NOT NULL,
                        `groupId` BIGINT unsigned NOT NULL,
                        `bucketId` BIGINT unsigned NULL,
                        `snapshot` JSON NULL,
                        `imageUrls` JSON NULL,
                        `content` TEXT NOT NULL,
                        `createdAt` TIMESTAMP NOT NULL,
                        `updatedAt` TIMESTAMP NOT NULL,
                        `postType` ENUM('post', 'news') NOT NULL,
                        CONSTRAINT `post_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`),
                        CONSTRAINT `post_groupid_fk` FOREIGN KEY (`groupId`) REFERENCES `Group`(`id`),
                        CONSTRAINT `post_bucketid_fk` FOREIGN KEY (`bucketId`) REFERENCES `Bucket`(`id`)
);
CREATE TABLE `PostLike` (
                            `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            `userId` CHAR(36) NOT NULL COMMENT '좋아요를 누른 사용자',
                            `postId` BIGINT unsigned NOT NULL COMMENT '좋아요를 받은 게시글',
                            `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 생성 시간',
                            CONSTRAINT `postlike_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                            CONSTRAINT `postlike_postid_fk` FOREIGN KEY (`postId`) REFERENCES `Post`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE `Comment` (
                           `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                           `postId` BIGINT unsigned NOT NULL,
                           `content` VARCHAR(255) NOT NULL,
                           `createdAt` TIMESTAMP NOT NULL,
                           `updatedAt` TIMESTAMP NOT NULL,
                           CONSTRAINT `comment_postid_fk` FOREIGN KEY (`postId`) REFERENCES `Post`(`id`)
);

CREATE TABLE `CommentLike` (
                               `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               `userId` CHAR(36) NOT NULL COMMENT '좋아요를 누른 사용자',
                               `commentId` BIGINT unsigned NOT NULL COMMENT '좋아요를 받은 댓글',
                               `createdAt` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '좋아요 생성 시간',
                               CONSTRAINT `commentlike_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                               CONSTRAINT `commentlike_commentid_fk` FOREIGN KEY (`commentId`) REFERENCES `Comment`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE `PortfolioTemplate` (
                                     `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                     `name` VARCHAR(255) NOT NULL COMMENT '포트폴리오 템플릿 이름',
                                     `depositWithdrawalRatio` DECIMAL(8, 2) NOT NULL,
                                     `savingTimeDepositRatio` DECIMAL(8, 2) NOT NULL,
                                     `investmentRatio` DECIMAL(8, 2) NOT NULL,
                                     `foreignCurrencyRatio` DECIMAL(8, 2) NOT NULL,
                                     `pensionRatio` DECIMAL(8, 2) NOT NULL,
                                     `etcRatio` DECIMAL(8, 2) NOT NULL,
                                     `createdAt` TIMESTAMP NOT NULL,
                                     `updatedAt` TIMESTAMP NOT NULL
);


CREATE TABLE `GoalPortfolio` (
                                 `id` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                 `userId` CHAR(36) NOT NULL,
                                 `depositWithdrawalRatio` INT NOT NULL,
                                 `savingTimeDepositRatio` INT NOT NULL,
                                 `investmentRatio` INT NOT NULL,
                                 `foreignCurrencyRatio` INT NOT NULL,
                                 `pensionRatio` INT NOT NULL,
                                 `etcRatio` INT NOT NULL,
                                 `templateId` BIGINT unsigned NOT NULL,
                                 `createdAt` TIMESTAMP NOT NULL,
                                 `updatedAt` TIMESTAMP NOT NULL,
                                 CONSTRAINT `goalportfolio_userid_fk` FOREIGN KEY (`userId`) REFERENCES `User`(`id`),
                                 CONSTRAINT `goalportfolio_templateid_fk` FOREIGN KEY (`templateId`) REFERENCES `PortfolioTemplate`(`id`)
);

