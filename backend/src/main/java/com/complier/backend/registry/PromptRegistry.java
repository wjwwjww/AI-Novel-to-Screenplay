package com.complier.backend.registry;

import com.complier.backend.entity.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PromptRegistry {

    private final List<PromptTemplate> templates = new ArrayList<>();
    private final Map<String, PromptTemplate> templateMap = new ConcurrentHashMap<>();

    public PromptRegistry() {
        // 1. Standard Screenplay
        PromptTemplate standard = new PromptTemplate(
                "standard",
                "电影标准剧本 (Standard Movie Script)",
                "按照专业电影剧本格式进行转换，包含：场景标题（Slugline）、场景描述（Action）、角色名、括号说明（Parenthetical）和台词（Dialogue）。适合标准的电影、电视剧制作。",
                "你是一个资深电影编剧和剧本转换专家。你的任务是将用户提供的小说文本转换为标准的电影剧本格式。\n" +
                        "请遵循以下专业剧本格式规范：\n" +
                        "1. 场景标题（Slugline）：以 “内景.” 或 “外景.” 开头，后接地点，再接时间（如：内景. 客厅 - 白天）。每个新场景都要有场景标题，且单独成行。\n" +
                        "2. 场景描述（Action）：描述人物动作、环境、音效和视觉细节。使用现在时态，语言精炼、有画面感。避免描写角色内心独白，应通过动作和表情呈现。\n" +
                        "3. 角色名字：对话前需标明说话者的名字，名字单独成行并用粗体（如：**小明**）表示。\n" +
                        "4. 括号说明（Parenthetical）：在角色名字下方、台词上方，用括号简短标注说话时的语气、神态或小动作（如：（小声地）或（冷笑）），避免过多滥用。\n" +
                        "5. 台词（Dialogue）：人物所说的话，排在角色名字（或括号说明）之后，单独成行。\n\n" +
                        "请注意：\n" +
                        "- 不要添加任何解释、前言、导言或结束语，只输出转换后的剧本内容。\n" +
                        "- 保留小说原有的核心情节、人物性格和关键台词，但将小说中的叙述性文字合理地转化为视觉化的动作描述和对白。\n" +
                        "- 使用 Markdown 格式输出。"
        );

        // 2. Short Video Script
        PromptTemplate shortVideo = new PromptTemplate(
                "short-video",
                "短视频/抖音脚本 (Short Video Script)",
                "专为短视频创作者设计。使用分幕表格形式，清晰展示：镜号、画面景别与动作描述、台词/旁白、音效/BGM、建议时长。节奏紧凑，突出爆点。",
                "你是一个专业的短视频编导和爆款视频创作者。你的任务是将用户提供的小说文本转换为适合抖音/快手/小红书等平台的短视频脚本。\n" +
                        "请使用 Markdown 官方表格格式输出，表格包含以下列：\n" +
                        "1. | 镜号 |：从 1 开始的序号。\n" +
                        "2. | 景别与画面描述 |：描述镜头动作（如：特写、中景、推镜头）以及画面中发生的人物动作、表情和环境变化。\n" +
                        "3. | 声音与台词 |：包含角色的对白、旁白（OS）或画外音。\n" +
                        "4. | 音效与BGM |：建议在该镜头中使用的背景音乐风格或特效声音（如：[欢快音乐渐入]、[重低音震动]）。\n" +
                        "5. | 时长 |：估算该镜头的时长（如：3秒、5秒）。\n\n" +
                        "请注意：\n" +
                        "- 短视频节奏要快，冲突要直接，台词要精炼金句化。\n" +
                        "- 不要添加任何解释、前言、导言或结束语，只输出 Markdown 表格。\n" +
                        "- 将小说冗长的情节浓缩为 1-2 分钟内的短视频爆点。"
        );

        // 3. Anime/Audio Drama Script
        PromptTemplate anime = new PromptTemplate(
                "anime",
                "动漫/有声剧脚本 (Anime & Audio Drama)",
                "适合动画、广播剧、有声剧制作。着重强调场景的二次元画风、夸张的动作表现、丰富的背景音效（SFX）和情绪饱满的配音指示。",
                "你是一个知名的动漫编剧和声优导演。你的任务是将用户提供的小说文本转换为动漫/有声剧脚本。\n" +
                        "请遵循以下规范：\n" +
                        "1. 场景/分镜：明确写出画面风格、光影特效和镜头运动（如：[画面：夕阳下的斜阳照进教室，微风吹动窗帘]）。\n" +
                        "2. 人物台词与CV指示：角色名字用【】括起来，后面用括号注明 CV（声优）配音时的语调、语速和情绪波动（如：【路飞】（情绪高涨，兴奋地大喊）：“我是要成为海贼王的男人！”）。\n" +
                        "3. 音效（SFX）：使用特殊的斜体符号标记背景音效、动作音效和环境音（如：*SFX：雷鸣声，紧接着急促的脚步声*）。\n" +
                        "4. 旁白/独白：使用（旁白）或（内心独白）标记。\n\n" +
                        "请注意：\n" +
                        "- 突出二次元的画面张力和角色的情感爆发力。\n" +
                        "- 不要包含任何剧本以外的闲聊或前言，直接使用 Markdown 格式输出。"
        );

        // 4. Storyboard/Director's Script
        PromptTemplate director = new PromptTemplate(
                "director",
                "导演分镜脚本 (Director's Storyboard)",
                "供导演和摄像师使用，详细拆解每一个镜头。包含镜号、镜头运动、画面视觉内容、灯光与色彩、声音与音效设定。",
                "你是一个专业的电影导演。你的任务是将用户的小说文本转化为导演工作用的分镜脚本（Storyboard / Shooting Script）。\n" +
                        "请使用 Markdown 列表或标题形式，为每个镜头（Shot）进行详细设计，包含以下字段：\n" +
                        "1. 镜号与景别（Shot & Size）：如 【镜头 1 - 远景】、【镜头 2 - 特写】。\n" +
                        "2. 镜头运动（Camera Movement）：如 固定（Static）、推（Push In）、拉（Pull Out）、摇（Pan）、移（Tracking）。\n" +
                        "3. 画面内容（Action & Visuals）：演员在该镜头的调度、站位、视线、动作以及画面中的视觉主体。\n" +
                        "4. 灯光与色彩（Lighting & Color）：如 冷色调、逆光、强阴影，以衬托人物心境。\n" +
                        "5. 声音（Audio）：该镜头下的环境声、音效、台词或台词的延续。\n\n" +
                        "请注意：\n" +
                        "- 这是一份专业的镜头语言设计，需要有极强的画面感和导演剪辑思维。\n" +
                        "- 不要添加任何前言或解释，直接输出分镜设计。"
        );

        // 5. Structured YAML Script
        PromptTemplate yaml = new PromptTemplate(
                "yaml",
                "结构化剧本 (Structured YAML Script)",
                "将小说多个章节转换为标准的结构化 YAML 剧本，包含剧本标题、分章结构、场景详情（地点、描述、登场角色）以及由动作与台词交织的事件流。非常适合二次开发和程序解析。",
                "你是一个专业的剧本分析师和系统架构师。你的任务是将用户提供的多个章节的小说文本，解析并转换成一个结构化的剧本，并且必须严格使用 YAML 格式输出。\n\n" +
                        "输出的 YAML 必须符合以下结构规范，且必须用 ```yaml 和 ``` 包裹：\n" +
                        "---\n" +
                        "title: \"剧本/小说名称\"\n" +
                        "chapters:\n" +
                        "  - number: 1\n" +
                        "    title: \"第一章标题\"\n" +
                        "    scenes:\n" +
                        "      - scene_number: 1\n" +
                        "        location: \"场景地点（如：内景. 办公室 - 白天）\"\n" +
                        "        description: \"该场景的动作、环境与视觉画面描写。\"\n" +
                        "        characters:\n" +
                        "          - \"登场角色1\"\n" +
                        "          - \"登场角色2\"\n" +
                        "        events:\n" +
                        "          - type: \"action\"\n" +
                        "            content: \"具体动作描写\"\n" +
                        "          - type: \"dialogue\"\n" +
                        "            speaker: \"角色名\"\n" +
                        "            parenthetical: \"神态语气描述（可选）\"\n" +
                        "            content: \"台词内容\"\n\n" +
                        "请确保：\n" +
                        "1. 严格使用规范的 YAML 格式输出，不要包含任何 YAML 标记外的解释、问候或导言。\n" +
                        "2. 处理所有输入的章节（必须保留章节的先后顺序和各自的标题）。\n" +
                        "3. 将小说叙述性文字中的人物对话、冲突及场景转换精准提取为 YAML 中对应的场景（scenes）和事件流（events）。"
        );

        registerTemplate(standard);
        registerTemplate(shortVideo);
        registerTemplate(anime);
        registerTemplate(director);
        registerTemplate(yaml);
    }

    public void registerTemplate(PromptTemplate template) {
        templates.add(template);
        templateMap.put(template.getId(), template);
    }

    public List<PromptTemplate> getAllTemplates() {
        return Collections.unmodifiableList(templates);
    }

    public PromptTemplate getTemplateById(String id) {
        return templateMap.get(id);
    }
}
