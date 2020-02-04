package com.algaworks.brewer.storage.s3;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {

	@Override
	public String salvar(MultipartFile[] files) {
		
		return null;
	}

	@Override
	public byte[] recuperarFoto(String foto) {
		
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		
		return null;
	}

	@Override
	public void excluir(String foto) {
		
		
	}

	@Override
	public String getUrl(String foto) {
		
		return null;
	}
}
