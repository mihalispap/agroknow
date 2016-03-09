
package com.agroknow.search.domain.repositories;

import com.agroknow.search.domain.entities.User;

/**
 *
 * @author aggelos
 */
public interface UserRepository {

    User findByToken(String token);

    User findByUserEmail(String email);

    User save(User user);

}
