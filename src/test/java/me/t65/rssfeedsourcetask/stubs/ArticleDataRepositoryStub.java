package me.t65.rssfeedsourcetask.stubs;

import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Component
public class ArticleDataRepositoryStub implements ArticlesRepository {
    @Override
    public void flush() {}

    @Override
    public <S extends ArticlesEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<ArticlesEntity> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public ArticlesEntity getOne(UUID uuid) {
        return null;
    }

    @Override
    public ArticlesEntity getById(UUID uuid) {
        return null;
    }

    @Override
    public ArticlesEntity getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ArticlesEntity> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ArticlesEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ArticlesEntity, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ArticlesEntity> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<ArticlesEntity> findById(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public List<ArticlesEntity> findAll() {
        return null;
    }

    @Override
    public List<ArticlesEntity> findAllById(Iterable<UUID> uuids) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {}

    @Override
    public void delete(ArticlesEntity entity) {}

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {}

    @Override
    public void deleteAll(Iterable<? extends ArticlesEntity> entities) {}

    @Override
    public void deleteAll() {}

    @Override
    public List<ArticlesEntity> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<ArticlesEntity> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public boolean existsHashlink(long hashedLink) {
        return false;
    }
}
