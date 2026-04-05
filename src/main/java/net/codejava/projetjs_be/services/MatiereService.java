package net.codejava.projetjs_be.services;

import lombok.RequiredArgsConstructor;
import net.codejava.projetjs_be.dto.MatiereDTO;
import net.codejava.projetjs_be.entities.*;
import net.codejava.projetjs_be.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<MatiereDTO> getMessMatieres(Long userId) {
        return matiereRepository.findByUtilisateurIdOrderByPrioriteAsc(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MatiereDTO creer(Long userId, MatiereDTO dto) {
        Utilisateur u = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Matiere m = Matiere.builder()
                .nom(dto.getNom())
                .priorite(dto.getPriorite())
                .objectifHebdoHeures(dto.getObjectifHebdoHeures())
                .progression(0f)
                .utilisateur(u)
                .build();
        return toDTO(matiereRepository.save(m));
    }

    public MatiereDTO modifier(Long userId, Long id, MatiereDTO dto) {
        Matiere m = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        if (!m.getUtilisateur().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        m.setNom(dto.getNom());
        m.setPriorite(dto.getPriorite());
        m.setObjectifHebdoHeures(dto.getObjectifHebdoHeures());
        return toDTO(matiereRepository.save(m));
    }

    public void supprimer(Long userId, Long id) {
        Matiere m = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        if (!m.getUtilisateur().getId().equals(userId))
            throw new RuntimeException("Accès refusé");
        matiereRepository.delete(m);
    }

    private MatiereDTO toDTO(Matiere m) {
        return MatiereDTO.builder()
                .id(m.getId()).nom(m.getNom())
                .priorite(m.getPriorite())
                .objectifHebdoHeures(m.getObjectifHebdoHeures())
                .progression(m.getProgression())
                .build();
    }
}