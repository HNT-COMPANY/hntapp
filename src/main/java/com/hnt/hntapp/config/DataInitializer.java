package com.hnt.hntapp.config;

import com.hnt.hntapp.domain.warehouse.entity.PhoneColor;
import com.hnt.hntapp.domain.warehouse.entity.PhoneModel;
import com.hnt.hntapp.domain.warehouse.entity.PhoneStorage;
import com.hnt.hntapp.domain.warehouse.repository.PhoneModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 앱 시작 시 기본 모델 데이터 자동 입력
 * - DB에 모델이 하나도 없을 때만 실행
 * - 이후 모델 추가/제거는 앱 내 모델 관리 화면에서 직접
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final PhoneModelRepository phoneModelRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (phoneModelRepository.count() > 0) {
            log.info("[DataInitializer] 모델 데이터 이미 존재 — 건너뜀");
            return;
        }

        log.info("[DataInitializer] 기본 모델 데이터 입력 시작...");
        initSamsungModels();
        log.info("[DataInitializer] 기본 모델 데이터 입력 완료");
    }

    private void initSamsungModels() {

        // ── 갤럭시 S26 ──────────────────────────────
        save("Samsung", "갤럭시 S26 (SM-S942N)", new Object[][]{
                {"256GB", 1254000L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }},
                {"512GB", 1507000L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }}
        });

        // ── 갤럭시 S26+ ─────────────────────────────
        save("Samsung", "갤럭시 S26+ (SM-S947N)", new Object[][]{
                {"256GB", 1452000L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }},
                {"512GB", 1705000L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }}
        });

        // ── 갤럭시 S26 Ultra ────────────────────────
        save("Samsung", "갤럭시 S26 Ultra (SM-S948N)", new Object[][]{
                {"256GB", 1797400L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }},
                {"512GB", 2537700L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }},
                {"1TB", 2545000L, new String[][]{
                        {"코발트 바이올렛", "#7B5EA7"}, {"스카이 블루", "#87CEEB"},
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }}
        });

        // ── 갤럭시 S25 ──────────────────────────────
        save("Samsung", "갤럭시 S25 (SM-S931N)", new Object[][]{
                {"256GB", 1155000L, new String[][]{
                        {"네이비", "#1B2A4A"}, {"아이스 블루", "#A8C4DC"},
                        {"실버 쉐도우", "#C0C0C0"}, {"민트", "#98D8C8"}
                }},
                {"512GB", 1298000L, new String[][]{
                        {"네이비", "#1B2A4A"}, {"아이스 블루", "#A8C4DC"},
                        {"실버 쉐도우", "#C0C0C0"}, {"민트", "#98D8C8"}
                }}
        });

        // ── 갤럭시 S25+ ─────────────────────────────
        save("Samsung", "갤럭시 S25+ (SM-S936N)", new Object[][]{
                {"256GB", 1353000L, new String[][]{
                        {"네이비", "#1B2A4A"}, {"아이스 블루", "#A8C4DC"},
                        {"실버 쉐도우", "#C0C0C0"}, {"민트", "#98D8C8"}
                }},
                {"512GB", 1496000L, new String[][]{
                        {"네이비", "#1B2A4A"}, {"아이스 블루", "#A8C4DC"},
                        {"실버 쉐도우", "#C0C0C0"}, {"민트", "#98D8C8"}
                }}
        });

        // ── 갤럭시 S25 Ultra ────────────────────────
        save("Samsung", "갤럭시 S25 Ultra (SM-S938N)", new Object[][]{
                {"256GB", 1698400L, new String[][]{
                        {"티타늄 실버블루", "#8CA8C0"}, {"티타늄 블랙", "#2A2A2A"},
                        {"티타늄 그레이", "#8A8A8A"}, {"티타늄 화이트실버", "#E8E8E0"}
                }},
                {"512GB", 1841400L, new String[][]{
                        {"티타늄 실버블루", "#8CA8C0"}, {"티타늄 블랙", "#2A2A2A"},
                        {"티타늄 그레이", "#8A8A8A"}, {"티타늄 화이트실버", "#E8E8E0"}
                }},
                {"1TB", 2127400L, new String[][]{
                        {"티타늄 실버블루", "#8CA8C0"}, {"티타늄 블랙", "#2A2A2A"},
                        {"티타늄 그레이", "#8A8A8A"}, {"티타늄 화이트실버", "#E8E8E0"}
                }}
        });

        // ── 갤럭시 S25 엣지 ─────────────────────────
        save("Samsung", "갤럭시 S25 엣지 (SM-S937N)", new Object[][]{
                {"256GB", 1496000L, new String[][]{
                        {"티타늄 실버", "#C0C0C0"}, {"티타늄 아이스블루", "#A8C4DC"},
                        {"티타늄 제트블랙", "#1C1C1E"}
                }},
                {"512GB", 1639000L, new String[][]{
                        {"티타늄 실버", "#C0C0C0"}, {"티타늄 아이스블루", "#A8C4DC"},
                        {"티타늄 제트블랙", "#1C1C1E"}
                }}
        });

        // ── 갤럭시 S25 FE ───────────────────────────
        save("Samsung", "갤럭시 S25 FE (SM-S731N)", new Object[][]{
                {"256GB", 946000L, new String[][]{
                        {"화이트", "#F5F5F5"}, {"제트 블랙", "#1C1C1E"},
                        {"네이비", "#1B2A4A"}, {"아이시 블루", "#A8C4DC"}
                }}
        });

        // ── 갤럭시 Z 폴드7 ──────────────────────────
        save("Samsung", "갤럭시 Z 폴드7 (SM-F966N)", new Object[][]{
                {"256GB", 2379300L, new String[][]{
                        {"실버 쉐도우", "#C0C0C0"}, {"블루 쉐도우", "#4A6FA5"},
                        {"제트 블랙", "#1C1C1E"}
                }},
                {"512GB", 2537700L, new String[][]{
                        {"실버 쉐도우", "#C0C0C0"}, {"블루 쉐도우", "#4A6FA5"},
                        {"제트 블랙", "#1C1C1E"}
                }},
                {"1TB", 2933700L, new String[][]{
                        {"실버 쉐도우", "#C0C0C0"}, {"블루 쉐도우", "#4A6FA5"},
                        {"제트 블랙", "#1C1C1E"}
                }}
        });

        // ── 갤럭시 Z 플립7 ──────────────────────────
        save("Samsung", "갤럭시 Z 플립7 (SM-F766N)", new Object[][]{
                {"256GB", 1485000L, new String[][]{
                        {"제트 블랙", "#1C1C1E"}, {"블루 쉐도우", "#4A6FA5"},
                        {"코랄 레드", "#FF6B6B"}
                }},
                {"512GB", 1643400L, new String[][]{
                        {"제트 블랙", "#1C1C1E"}, {"블루 쉐도우", "#4A6FA5"},
                        {"코랄 레드", "#FF6B6B"}
                }}
        });

        // ── 갤럭시 Z 플립7 FE ───────────────────────
        save("Samsung", "갤럭시 Z 플립7 FE (SM-F761N)", new Object[][]{
                {"256GB", 1199000L, new String[][]{
                        {"블랙", "#1C1C1E"}, {"화이트", "#F5F5F5"}
                }}
        });

        // ── 갤럭시 A36 5G ───────────────────────────
        save("Samsung", "갤럭시 A36 5G (SM-A336N)", new Object[][]{
                {"128GB", 499400L, new String[][]{
                        {"어썸 라벤더", "#C9B1D9"}, {"어썸 화이트", "#F5F5F5"},
                        {"어썸 블랙", "#1C1C1E"}
                }}
        });

        // ── 갤럭시 와이드 8 ─────────────────────────
        save("Samsung", "갤럭시 와이드8 (SM-M166S)", new Object[][]{
                {"128GB", 374000L, new String[][]{
                        {"블랙", "#1C1C1E"}, {"라이트 핑크", "#FFB6C1"},
                        {"라이트 그린", "#90EE90"}
                }}
        });

        // ── 갤럭시 A17 LTE ──────────────────────────
        save("Samsung", "갤럭시 A17 LTE (SM-A175N)", new Object[][]{
                {"128GB", 319000L, new String[][]{
                        {"블랙", "#1C1C1E"}, {"그레이", "#8E8E93"},
                        {"라이트 블루", "#ADD8E6"}
                }}
        });

        // ── 갤럭시 퀀텀6 ────────────────────────────
        save("Samsung", "갤럭시 퀀텀6 (SM-A566S)", new Object[][]{
                {"128GB", 618200L, new String[][]{
                        {"어썸 라이트 그레이", "#D1D1D6"}, {"어썸 그라파이트", "#3A3A3C"}
                }}
        });

        // ── ZEM폰 포켓피스 ──────────────────────────
        save("Samsung", "ZEM폰 포켓피스 (SM-A175N_ZEM)", new Object[][]{
                {"128GB", 349800L, new String[][]{
                        {"라이트 블루", "#ADD8E6"}
                }}
        });
    }

    // ──────────────────────────────────────────
    // 헬퍼 — 모델/용량/컬러 한번에 저장
    // ──────────────────────────────────────────

    /**
     * @param storageData Object[][] — {capacity, releasePrice, String[][colors]}
     */
    private void save(String maker, String name, Object[][] storageData) {
        PhoneModel model = PhoneModel.builder()
                .maker(maker)
                .name(name)
                .build();

        for (Object[] sd : storageData) {
            String   capacity     = (String) sd[0];
            Long     releasePrice = (Long)   sd[1];
            String[][] colorData  = (String[][]) sd[2];

            PhoneStorage storage = PhoneStorage.builder()
                    .phoneModel(model)
                    .capacity(capacity)
                    .releasePrice(releasePrice)
                    .build();

            for (String[] cd : colorData) {
                PhoneColor color = PhoneColor.builder()
                        .phoneStorage(storage)
                        .colorName(cd[0])
                        .hexCode(cd[1])
                        .build();
                storage.getColors().add(color);
            }
            model.getStorages().add(storage);
        }

        phoneModelRepository.save(model);
        log.info("[DataInitializer] 저장 완료: {} {}", maker, name);
    }
}
