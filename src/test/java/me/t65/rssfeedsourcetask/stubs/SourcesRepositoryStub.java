package me.t65.rssfeedsourcetask.stubs;

import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class SourcesRepositoryStub implements SourcesRepository {
    @Override
    public void flush() {}

    @Override
    public <S extends SourcesEntity> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends SourcesEntity> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<SourcesEntity> entities) {}

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {}

    @Override
    public void deleteAllInBatch() {}

    @Override
    public SourcesEntity getOne(Integer integer) {
        return null;
    }

    @Override
    public SourcesEntity getById(Integer integer) {
        return null;
    }

    @Override
    public SourcesEntity getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends SourcesEntity> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends SourcesEntity> List<S> findAll(Example<S> example) {
        return Collections.emptyList();
    }

    @Override
    public <S extends SourcesEntity> List<S> findAll(Example<S> example, Sort sort) {
        return Collections.emptyList();
    }

    @Override
    public <S extends SourcesEntity> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends SourcesEntity> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends SourcesEntity> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends SourcesEntity, R> R findBy(
            Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends SourcesEntity> S save(S entity) {
        return null;
    }

    @Override
    public <S extends SourcesEntity> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<SourcesEntity> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public List<SourcesEntity> findAll() {
        return Collections.emptyList();
    }

    @Override
    public List<SourcesEntity> findAllById(Iterable<Integer> integers) {
        return Collections.emptyList();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {}

    @Override
    public void delete(SourcesEntity entity) {}

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {}

    @Override
    public void deleteAll(Iterable<? extends SourcesEntity> entities) {}

    @Override
    public void deleteAll() {}

    @Override
    public List<SourcesEntity> findAll(Sort sort) {
        return Collections.emptyList();
    }

    @Override
    public Page<SourcesEntity> findAll(Pageable pageable) {
        return null;
    }
}
