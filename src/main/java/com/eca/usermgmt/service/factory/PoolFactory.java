package com.eca.usermgmt.service.factory;


import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class PoolFactory extends BasePooledObjectFactory<Matcher> {
	static final String REG_EXP = ".*[;'\\\\\"{}$].*";
	private final Pattern pattern = Pattern.compile(REG_EXP);
	private GenericObjectPool<Matcher> matcherPool = null;

	@Value("${app.pool.minIdle}")
	private Integer minIdle;

	@Value("${app.pool.maxIdle}")
	private Integer maxIdle;

	@Value("${app.pool.maxTotal}")
	private Integer maxTotal;

	@Override
	public Matcher create() {
		return this.pattern.matcher("");
	}

	@Override
	public PooledObject<Matcher> wrap(Matcher matcher) {
		return new DefaultPooledObject<>(matcher);
	}

	@PostConstruct
	public void init() {
		var poolConfig = new GenericObjectPoolConfig<Matcher>();
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMaxTotal(maxTotal);
		this.matcherPool = new GenericObjectPool<>(new PoolFactory(), poolConfig);
		IntStream.range(1, maxIdle).mapToObj(x -> {
					try {
						return matcherPool.borrowObject();
					} catch (Exception e) {
						return null;
					}
				}).collect(Collectors.toList()).stream().filter(Objects::nonNull)
				.forEach(y -> this.matcherPool.returnObject(y));

	}

	public GenericObjectPool<Matcher> getMatcherPool() {
		return matcherPool;
	}
}
