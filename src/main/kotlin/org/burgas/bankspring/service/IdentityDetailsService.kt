package org.burgas.bankspring.service

import org.burgas.bankspring.dao.identity.IdentityDetails
import org.burgas.bankspring.mapper.IdentityMapper
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
class IdentityDetailsService : UserDetailsService {

    private final val identityMapper: IdentityMapper

    constructor(identityMapper: IdentityMapper) {
        this.identityMapper = identityMapper
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return IdentityDetails(
            this.identityMapper.identityRepository.findIdentityByEmail(username)
                .orElseThrow { throw IllegalArgumentException("Identity not found by email") }
        )
    }
}