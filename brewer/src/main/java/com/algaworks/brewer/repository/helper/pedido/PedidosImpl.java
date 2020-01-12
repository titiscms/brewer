package com.algaworks.brewer.repository.helper.pedido;

import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Pedido;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.filter.PedidoFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class PedidosImpl implements PedidosQueries {

	
	@Autowired
	private PaginacaoUtil paginacaoUtil;
	
	@PersistenceContext
	private EntityManager manager;
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Pedido> filtrar(PedidoFilter filtro, Pageable pageable) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Pedido.class);
		
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(filtro, criteria);
		
		return new PageImpl<>(criteria.list(), pageable, total(filtro));
	}
	
	@Transactional(readOnly = true)
	@Override
	public Pedido buscarComItens(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Pedido.class);
		criteria.createAlias("itens", "i", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Pedido) criteria.uniqueResult();
	}

	private Long total(PedidoFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Pedido.class);
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	private void adicionarFiltro(PedidoFilter filtro, Criteria criteria) {
		criteria.createAlias("cliente", "c");
		
		if(filtro != null) {
			if(!StringUtils.isEmpty(filtro.getCodigo())) {
				criteria.add(Restrictions.eq("codigo", filtro.getCodigo()));
			}
			if(!StringUtils.isEmpty(filtro.getStatus())) {
				criteria.add(Restrictions.eq("status", filtro.getStatus()));
			}
			if(!StringUtils.isEmpty(filtro.getDataInicio())) {
				LocalDateTime dataInicio = LocalDateTime.of(filtro.getDataInicio(), LocalTime.of(0, 0));
				criteria.add(Restrictions.ge("dataCriacao", dataInicio));
			}
			if(!StringUtils.isEmpty(filtro.getDataFim())) {
				LocalDateTime dataFim = LocalDateTime.of(filtro.getDataFim(), LocalTime.of(23, 59));
				criteria.add(Restrictions.le("dataCriacao", dataFim));
			}
			if(!StringUtils.isEmpty(filtro.getValorMin())) {
				criteria.add(Restrictions.ge("valorTotal", filtro.getValorMin()));
			}
			if(!StringUtils.isEmpty(filtro.getValorMax())) {
				criteria.add(Restrictions.le("valorTotal", filtro.getValorMax()));
			}
			if(!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteria.add(Restrictions.ilike("c.nome", filtro.getNomeCliente(), MatchMode.ANYWHERE));
			}
			if(!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteria.add(Restrictions.eq("c.cpfOuCnpj", TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
		}
	}
}
