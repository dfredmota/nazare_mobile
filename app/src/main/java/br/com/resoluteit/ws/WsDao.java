package br.com.resoluteit.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import br.com.resoluteit.model.PesquisaPreco;
import br.com.resoluteit.model.Usuario;


public class WsDao {


	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");


	public static Usuario loginApp(String matricula, String senha) {
		Connection con = null;
		PreparedStatement ps = null;
		Usuario usuario = null;

		try {
			con = DataConnect.getConnection();
			ps = con.prepareStatement("Select * from usuarios_app where matricula = ? and senha = ?");
			ps.setString(1, matricula);
			ps.setString(2, senha);

			ResultSet rs = ps.executeQuery();

			System.out.println(ps);

			if (rs.next()) {

				usuario = new Usuario();

				usuario.setId(rs.getInt("id"));
				usuario.setNome(rs.getString("nome"));
				usuario.setMatricula(rs.getString("matricula"));
				usuario.setSenha(rs.getString("senha"));

			}

		} catch (SQLException ex) {
			System.out.println("Login error -->" + ex.getMessage());
			return usuario;
		} finally {
			DataConnect.close(con);
		}
		return usuario;
	}


	public static List<PesquisaPreco> sincronizaPesquisaPreco(){

		Connection con = null;
		PreparedStatement ps = null;
		List<PesquisaPreco> lista = new ArrayList<PesquisaPreco>();


		String sql = "select pp.id,pp.concorrente,pp.ean,pp.secao,pp.grupo,pp.sub_grupo,pp.descricao,pp.preco,pp.flag,pp.id_arquivo\n" +
				      " from pesquisa_preco pp,arquivos ar where pp.id_arquivo = ar.id and ar.sincronizado = 'N'";

		try {

			con = DataConnect.getConnection();

			ps = con.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while(rs.next()) {

			PesquisaPreco pp = new PesquisaPreco();

			pp.setId(rs.getInt("id"));
			pp.setConcorrente(rs.getString("concorrente"));
			pp.setEan(rs.getString("ean"));
			pp.setSecao(rs.getString("secao"));
			pp.setGrupo(rs.getString("grupo"));
			pp.setSubGrupo(rs.getString("sub_grupo"));
			pp.setDescricao(rs.getString("descricao"));
			pp.setPreco(rs.getString("preco"));
			pp.setFlag(rs.getString("flag"));
			pp.setIdArquivo(rs.getInt("id_arquivo"));

			lista.add(pp);

			}

		}catch(Exception e){
			e.printStackTrace();
		}
		 finally {
			DataConnect.close(con);
		}


		return lista;

	}


	public static void atualizarArquivoParaSincronizado(Integer idArquivo,Integer idUsuarioSincronismo){

		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;

		try {

			String sql = "update arquivos set sincronizado='S',id_usuario="+idUsuarioSincronismo+" where id="+idArquivo;

			con = DataConnect.getConnection();

			ps = con.prepareStatement(sql);

			System.out.println(ps);

			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

}