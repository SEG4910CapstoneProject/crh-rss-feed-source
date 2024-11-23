package me.t65.rssfeedsourcetask.stubs;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class ArticleContentRepositoryStub implements ArticleContentRepository {
    @Override
    public <S extends ArticleContentEntity> S insert(S entity) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> List<S> insert(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ArticleContentEntity> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ArticleContentEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ArticleContentEntity, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ArticleContentEntity> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ArticleContentEntity> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<ArticleContentEntity> findAll() {
        return null;
    }

    @Override
    public List<ArticleContentEntity> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {}

    @Override
    public void delete(ArticleContentEntity entity) {}

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {}

    @Override
    public void deleteAll(Iterable<? extends ArticleContentEntity> entities) {}

    @Override
    public void deleteAll() {}

    @Override
    public List<ArticleContentEntity> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ArticleContentEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public ArticleContentEntity findByLink(String link) {
        return null;
    }
}
