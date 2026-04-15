package org.burgas.bankspring.redis

import org.burgas.bankspring.dao.Entity
import org.springframework.stereotype.Component

@Component
interface RedisCacheHandler<in E : Entity> {

    fun handleCache(entity: E)
}