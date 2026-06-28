package com.hoodle.orbitorder.Repository;

import com.hoodle.orbitorder.Entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {

}
