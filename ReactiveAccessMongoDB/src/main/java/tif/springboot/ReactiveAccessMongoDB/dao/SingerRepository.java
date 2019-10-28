package tif.springboot.ReactiveAccessMongoDB.dao;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tif.springboot.ReactiveAccessMongoDB.bean.Singer;

public interface SingerRepository extends ReactiveCrudRepository<Singer, String>
                                            ,ReactiveQueryByExampleExecutor<Singer>
{

}
